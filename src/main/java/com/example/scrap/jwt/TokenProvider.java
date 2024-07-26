package com.example.scrap.jwt;

import com.example.scrap.base.data.DefaultData;
import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.MemberLog;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.jwt.dto.TokenType;
import com.example.scrap.web.member.dto.MemberDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

@Component
@Slf4j
public class TokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.expire_day.access}")
    private int expireDayOfAccessToken;

    @Value("${jwt.expire_day.refresh}")
    private int expireDayOfRefreshToken;

    /* 토큰 발급 **/
    /**
     * 토큰 발급하기
     */
    public Token createToken(Member member){

        MemberDTO memberDTO = new MemberDTO(member);

        String accessToken = createToken(member, expireDayOfAccessToken, TokenType.ACCESS);

        String refreshToken = createRefreshToken(memberDTO, member.getMemberLog().getRefreshTokenId(), expireDayOfRefreshToken);

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 토큰 생성하기
     */

    private String createToken(Member member, int expireDay, TokenType tokenType){
        MemberDTO memberDTO = new MemberDTO(member);

        return this.createToken(memberDTO, parseDayToMs(expireDay), tokenType);
    }

    /**
     * 토큰 생성하기
     */
    // [TODO] 토큰에 어떤 값을 담을지 고민 필요.
    // https://velog.io/@kimcno3/Jwt-Token%EC%97%90-%EB%8B%B4%EA%B8%B8-%EC%82%AC%EC%9A%A9%EC%9E%90-%EC%A0%95%EB%B3%B4%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B2%B0%EC%A0%95%EA%B3%BC-%ED%91%9C%EC%A4%80%EC%97%90-%EB%8C%80%ED%95%9C-%EC%9D%B4%ED%95%B4
    // snsId는 한번 노출되면 더이상 바꿀 수 있는 값이 아니라 위험할 수 있다는 생각 + 그렇다고 memberId만 넣자니 memberId가 sequence화 된거라 key가 뚤릴 시 위험할 수 있다는 생각.
    // 그렇다고 memberId를 넣지 않자니, 인터셉터와 서비스에서 각각 member를 찾기 위해 sql을 총 2번 실행하게 된다는 성능상의 문제.
    // 내가 생각한 결론: snsType, snsId, memberId를 다 넣는다. 그리고 snsType과 snsId와 memberId를 종합해서 member를 찾는다.
    private String createToken(MemberDTO memberDTO, long expireMs, TokenType tokenType){
        Claims claims = Jwts.claims();
        claims.put("snsType", memberDTO.getSnsType());
        claims.put("snsId", memberDTO.getSnsId());
        claims.setAudience(memberDTO.getMemberId().toString());
        claims.put("type", tokenType);

        long currentTimeMills = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTimeMills))
                .setExpiration(new Date(currentTimeMills + expireMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();
    }

    /**
     * 갱신 토큰 생성
     */
    private String createRefreshToken(MemberDTO memberDTO, Long prevRefreshTokenId, long expireMs){
        Claims claims = Jwts.claims();
        claims.put("snsType", memberDTO.getSnsType());
        claims.put("snsId", memberDTO.getSnsId());
        claims.put("type", TokenType.REFRESH);

        // refreshToken id 생성. 단 이전 id와 다른값으로 발급하기
        Random random = new Random();
        Long refreshTokenId = random.nextLong();
        while(refreshTokenId.equals(prevRefreshTokenId)){
            refreshTokenId = random.nextLong();
        }

        long currentTimeMills = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTimeMills))
                .setExpiration(new Date(currentTimeMills + expireMs))
                .setId(refreshTokenId.toString())
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();
    }

    /**
     * accessToken 재발급하기
     */
    public Token reissueAccessToken(String refreshToken){

        MemberDTO memberDTO = parseMemberDTO(refreshToken);

        String reissuedAccessToken = createToken(memberDTO, parseDayToMs(expireDayOfAccessToken), TokenType.ACCESS);

        return Token.builder()
                .accessToken(reissuedAccessToken)
                .build();
    }

    /**
     * refresh 토큰 재발급하기
     * 오직 accessToken으로만 refresh 토큰 재발급 가능
     */
    public Token reissueRefreshToken(Token token){

        MemberDTO memberDTO = parseMemberDTO(token.getRefreshToken());
        String reissuedRefreshToken = createToken(memberDTO, parseDayToMs(expireDayOfRefreshToken), TokenType.REFRESH);

        return Token.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(reissuedRefreshToken)
                .build();
    }
    /* 토큰 발급 끝 **/

    /* 토큰 유효성 검사 **/

    /**
     * 토큰 유효성 검사.
     * @throws AuthorizationException TOKEN_EXPIRED 토큰 만료시
     * @throws AuthorizationException TOKEN_NOT_VALID 잘못된 토큰일 시
     */
    public boolean isTokenValid(String token) {
        try{
            Jwts.parser().setSigningKey(jwtSecretKey)
                    .parseClaimsJws(token);

            return true;
        }
        catch (ExpiredJwtException e){
            throw new AuthorizationException(ErrorCode.TOKEN_EXPIRED);
        }
        catch (Exception e){
            throw new AuthorizationException(ErrorCode.TOKEN_NOT_VALID);
        }
    }

    /**
     * 토큰 타입이 Access인지 겁사
     */
    public boolean isTokenTypeIsAccess(String token){
        return TokenType.ACCESS.name()
                .equals(
                        Jwts.parser().setSigningKey(jwtSecretKey)
                                .parseClaimsJws(token)
                                .getBody()
                                .get("type", String.class)
                );
    }

    /**
     * 토큰 타입이 Refressh인지 겁사
     */
    public boolean isTokenTypeIsRefresh(String token){
        return TokenType.REFRESH.name()
                .equals(
                    Jwts.parser().setSigningKey(jwtSecretKey)
                            .parseClaimsJws(token)
                            .getBody()
                            .get("type", String.class)
                );
    }
    /* 토큰 유효성 검사 끝 **/

    /**
     * 토큰을 MemberDTO로 변환
     * @throws AuthorizationException 토큰 만료시, 잘못된 TokenType일시
     */
    public MemberDTO parseMemberDTO(String token){
        token = removeTokenPrefix(token);

        Claims claims = Jwts.parser().setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody();

        // 토큰의 값 조회
        SnsType snsType;
        try {
            snsType = SnsType.valueOf(claims.get("snsType", String.class));
        } catch (IllegalArgumentException e){
            throw new AuthorizationException(ErrorCode.TOKEN_TYPE_ILLEGAL);
        }

        String snsId = claims.get("snsId", String.class);
        Long memberId = Long.parseLong(claims.getAudience());

        return new MemberDTO(memberId, snsType, snsId);
    }

    /**
     * 토큰 prefix 지우기
     */
    public String removeTokenPrefix(String token){
        return token.replace(DefaultData.AUTH_PREFIX, "");
    }

    /**
     * day를 밀리초로 변환
     */
    private long parseDayToMs(int day){
        return day * 60L * 60L * 24L * 1000L; // 초, 분, 시간, 밀리단위
    }

    /**
     * hour를 밀리초로 변환
     */
    private long pareHourToMs(int hour) {
        return hour * 60L * 60L * 1000L; // 초, 분, 밀리단위
    }
}
