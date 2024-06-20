package com.example.scrap.jwt;

import com.example.scrap.base.Data;
import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.jwt.dto.TokenType;
import com.example.scrap.web.member.dto.MemberDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

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

        String accessToken = createToken(member, expireDayOfAccessToken, TokenType.ACCESS);

        String refreshToken = createToken(member, expireDayOfRefreshToken, TokenType.REFRESH);

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 토큰 생성하기
     */
    // [TODO] 토큰에 어떤 값을 담을지 고민 필요.
    // https://velog.io/@kimcno3/Jwt-Token%EC%97%90-%EB%8B%B4%EA%B8%B8-%EC%82%AC%EC%9A%A9%EC%9E%90-%EC%A0%95%EB%B3%B4%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B2%B0%EC%A0%95%EA%B3%BC-%ED%91%9C%EC%A4%80%EC%97%90-%EB%8C%80%ED%95%9C-%EC%9D%B4%ED%95%B4
    // snsId는 한번 노출되면 더이상 바꿀 수 있는 값이 아니라 위험할 수 있다는 생각 + 그렇다고 memberId만 넣자니 memberId가 sequence화 된거라 key가 뚤릴 시 위험할 수 있다는 생각.
    // 그렇다고 memberId를 넣지 않자니, 인터셉터와 서비스에서 각각 member를 찾기 위해 sql을 총 2번 실행하게 된다는 성능상의 문제.
    // 내가 생각한 결론: snsType, snsId, memberId를 다 넣는다. 그리고 snsType과 snsId와 memberId를 종합해서 member를 찾는다.
    private String createToken(Member member, int expireDay, TokenType tokenType){
        MemberDTO memberDTO = new MemberDTO(member);

        return this.createToken(memberDTO, expireDay, tokenType);
    }

    /**
     * 토큰 생성하기
     */
    private String createToken(MemberDTO memberDTO, int expireDay, TokenType tokenType){
        Claims claims = Jwts.claims();
        claims.put("snsType", memberDTO.getSnsType());
        claims.put("snsId", memberDTO.getSnsId());
        claims.setAudience(memberDTO.getMemberId().toString());
        claims.put("type", tokenType);

        long currentTimeMills = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTimeMills))
                .setExpiration(new Date(currentTimeMills + parseDayToMs(expireDay)))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();
    }

    /**
     * access 토큰 재발급하기
     * @throws AuthorizationException accessToken과 refreshToken의 member가 다르면
     */
    public Token reissueAccessToken(Token token){

        // accessToken과 refreshToken의 snsType과 id 같은지 확인
        if(isMemberOfTokenSame(token)){
            throw new AuthorizationException(ErrorCode.ACCESS_MEMBER_AND_REFRESH_MEMBER_NOT_MATCH);
        }

        MemberDTO memberDTO = parseMemberDTO(token.getAccessToken());
        String reissuedAccessToken = createToken(memberDTO, expireDayOfAccessToken, TokenType.ACCESS);

        return Token.builder()
                .accessToken(reissuedAccessToken)
                .refreshToken(token.getRefreshToken())
                .build();
    }

    /**
     * refresh 토큰 재발급하기
     * @throws AuthorizationException accessToken과 refreshToken의 member가 다르면
     */
    public Token reissueRefreshToken(Token token){

        // accessToken과 refreshToken의 snsType과 id 같은지 확인
        if(isMemberOfTokenSame(token)){
            throw new BaseException(ErrorCode.ACCESS_MEMBER_AND_REFRESH_MEMBER_NOT_MATCH);
        }

        MemberDTO memberDTO = parseMemberDTO(token.getRefreshToken());
        String reissuedRefreshToken = createToken(memberDTO, expireDayOfRefreshToken, TokenType.REFRESH);

        return Token.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(reissuedRefreshToken)
                .build();
    }
    /* 토큰 발급 끝 **/

    /* 토큰 유효성 검사 **/
    /**
     * access토큰과 refresh토큰이 동일한 유저의 것인지 확인
     * @return if member of accessToken and member of refreshToken is same, return true. else return false.
     */
    public boolean isMemberOfTokenSame(Token token){
        if(token == null){
            return false;
        }

        MemberDTO accessOfMemberDTO = parseMemberDTO(token.getAccessToken());
        MemberDTO refreshOfMemberDTO = parseMemberDTO(token.getAccessToken());

        return accessOfMemberDTO.equals(refreshOfMemberDTO);
    }

    /**
     * 토큰 유효성 검사.
     * @return if token expire or fail to parse, return true. else return false.
     */
    public boolean isTokenExpired(String token){
        try {
            token = token.replace(Data.AUTH_PREFIX, "");
//            Date expireDate = Jwts.parser().setSigningKey(jwtSecretKey)
//                    .parseClaimsJws(token)
//                    .getBody()
//                    .getExpiration();
//
//            log.info("토큰 만료일: {}, 현재 시간: {}", expireDate, new Date());

            return Jwts.parser().setSigningKey(jwtSecretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());

        } catch (Exception ex) {
            log.info("토큰 parse 실패: {}", ex.getMessage());
            ex.printStackTrace();
            return true;
        }
    }

    /**
     * 토큰 타입이 Access인지 겁사
     */
    public boolean isTokenTypeIsAccess(String token){
        try {
            token = token.replace(Data.AUTH_PREFIX, "");

            return TokenType.ACCESS.name().equals(
                    Jwts.parser().setSigningKey(jwtSecretKey)
                            .parseClaimsJws(token)
                            .getBody()
                            .get("type", String.class)
            );

        } catch (Exception e) {
            log.info("토큰 parse 실패: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    /* 토큰 유효성 검사 끝 **/

    /**
     * 토큰을 MemberDTO로 변환
     * @throws AuthorizationException 토큰 만료시
     */
    public MemberDTO parseMemberDTO(String token){
        token = token.replace(Data.AUTH_PREFIX, "");

        if(isTokenExpired(token)){
            throw new AuthorizationException(ErrorCode.TOKEN_EXPIRED);
        }

        Claims claims = Jwts.parser().setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody();

        SnsType snsType = SnsType.valueOf(claims.get("snsType", String.class));
        String snsId = claims.get("snsId", String.class);
        Long memberId = Long.parseLong(claims.getAudience());

        return new MemberDTO(memberId, snsType, snsId);
    }

    /**
     * day를 밀리초로 변환
     */
    private long parseDayToMs(int day){
        return day * 60L * 60L * 24L * 1000L; // 초, 분, 시간, 밀리단위
    }
}
