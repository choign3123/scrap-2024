package com.example.scrap.jwt;

import com.example.scrap.base.data.DefaultData;
import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.jwt.dto.TokenType;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.expire_day.access}")
    private int expireDayOfAccessToken;

    @Value("${jwt.expire_day.refresh}")
    private int expireDayOfRefreshToken;

    private final IMemberQueryService memberQueryService;

    /* 토큰 발급 **/
    /**
     * 토큰 발급하기
     */
    public Token createToken(Member member){

        MemberDTO memberDTO = new MemberDTO(member);

        // accessToken 생성
        String accessToken = createAccessToken(memberDTO);

        // 기존과 다른 refreshToken id 생성.
        Long newRefreshTokenId = member.getMemberLog().createRefreshTokenId();
        member.setRefreshTokenId(newRefreshTokenId);
        // refreshToken 생성
        String refreshToken = createRefreshToken(memberDTO, newRefreshTokenId);

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * token 갱신하기
     * @param refreshToken refreshToken으로 토큰 갱신함
     * @throws IllegalArgumentException refresh 토큰이 아닐 경우
     */
    public Token reissueToken(String refreshToken){
        refreshToken = removeTokenPrefix(refreshToken);

        // refresh 토큰이 아닐경우
        if(!equalsTokenType(refreshToken, TokenType.REFRESH)){
            throw new IllegalArgumentException("refresh 토큰이 아님");
        }

        Member member = findMemberByRefreshToken(refreshToken);

        // 이미 사용된 refreshToken인지 확인 (일회용)
        Long refreshTokenId = Long.parseLong(getRefreshTokenId(refreshToken));
        if(!member.getMemberLog().equalRefreshTokenId(refreshTokenId)){
            throw new AuthorizationException(ErrorCode.REFRESH_TOKEN_ALREADY_USED);
        }

        MemberDTO memberDTO = new MemberDTO(member);
        // access 토큰 갱신
        String reissuedAccessToken = createAccessToken(memberDTO);

        // 기존과 다른 refreshToken id 생성.
        Long newRefreshTokenId = member.getMemberLog().createRefreshTokenId();
        member.setRefreshTokenId(newRefreshTokenId);
        // refresh 토큰 재발급
        String reissuedRefreshToken = createRefreshToken(memberDTO, newRefreshTokenId);

        return Token.builder()
                .accessToken(reissuedAccessToken)
                .refreshToken(reissuedRefreshToken)
                .build();
    }

    /**
     * 접근 토큰 생성하기
     * @throws NullPointerException memberId가 null 일때
     */
    // [TODO] 토큰에 어떤 값을 담을지 고민 필요.
    // https://velog.io/@kimcno3/Jwt-Token%EC%97%90-%EB%8B%B4%EA%B8%B8-%EC%82%AC%EC%9A%A9%EC%9E%90-%EC%A0%95%EB%B3%B4%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B2%B0%EC%A0%95%EA%B3%BC-%ED%91%9C%EC%A4%80%EC%97%90-%EB%8C%80%ED%95%9C-%EC%9D%B4%ED%95%B4
    // snsId는 한번 노출되면 더이상 바꿀 수 있는 값이 아니라 위험할 수 있다는 생각 + 그렇다고 memberId만 넣자니 memberId가 sequence화 된거라 key가 뚤릴 시 위험할 수 있다는 생각.
    // 그렇다고 memberId를 넣지 않자니, 인터셉터와 서비스에서 각각 member를 찾기 위해 sql을 총 2번 실행하게 된다는 성능상의 문제.
    // 내가 생각한 결론: snsType, snsId, memberId를 다 넣는다. 그리고 snsType과 snsId와 memberId를 종합해서 member를 찾는다.
    private String createAccessToken(MemberDTO memberDTO){
        Claims claims = Jwts.claims();
        claims.put("snsType", memberDTO.getSnsType());
        claims.put("snsId", memberDTO.getSnsId());
        claims.put("type", TokenType.ACCESS);
        claims.setAudience(memberDTO.getMemberId().orElseThrow(
                () -> new NullPointerException("memberId가 null임")
        ).toString());

        long currentTimeMills = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTimeMills))
                .setExpiration(new Date(currentTimeMills + parseDayToMs(expireDayOfAccessToken)))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();
    }

    /**
     * 갱신 토큰 생성
     */
    private String createRefreshToken(MemberDTO memberDTO, Long refreshTokenId){
        Claims claims = Jwts.claims();
        claims.put("snsType", memberDTO.getSnsType());
        claims.put("snsId", memberDTO.getSnsId());
        claims.put("type", TokenType.REFRESH);

        long currentTimeMills = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTimeMills))
                .setExpiration(new Date(currentTimeMills + parseDayToMs(expireDayOfRefreshToken)))
                .setId(refreshTokenId.toString())
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();
    }
    /* 토큰 발급 끝 **/

    /* 토큰 유효성 검사 **/

    /**
     * 토큰 유효성 검사.
     * @throws AuthorizationException TOKEN_EXPIRED 토큰 만료시
     * @throws AuthorizationException TOKEN_NOT_VALID 잘못된 토큰일 시
     */
    public boolean isTokenValid(String token) {
        token = removeTokenPrefix(token);

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
     * 토큰 타입 겁사
     */
    public boolean equalsTokenType(String token, TokenType tokenType){
        token = removeTokenPrefix(token);

        return tokenType.name()
                .equals(
                        Jwts.parser().setSigningKey(jwtSecretKey)
                                .parseClaimsJws(token)
                                .getBody()
                                .get("type", String.class)
                );
    }
    /* 토큰 유효성 검사 끝 **/

    /**
     * access 토큰을 MemberDTO로 변환
     * @throws IllegalArgumentException access 토큰이 아닐시
     */
    public MemberDTO parseAccessToMemberDTO(String accessToken){
        accessToken = removeTokenPrefix(accessToken);

        // access 토큰인지 검사
        if(!equalsTokenType(accessToken, TokenType.ACCESS)){
           throw new IllegalArgumentException("access 토큰이 아님");
        }

        Claims claims = Jwts.parser().setSigningKey(jwtSecretKey)
                .parseClaimsJws(accessToken)
                .getBody();

        // 토큰의 값 조회
        String snsId = claims.get("snsId", String.class);
        Long memberId = Long.parseLong(claims.getAudience());

        SnsType snsType = null;
        try {
            snsType = SnsType.valueOf(claims.get("snsType", String.class));
        } catch (IllegalArgumentException e){
            throw new AuthorizationException(ErrorCode.TOKEN_TYPE_ILLEGAL);
        }

        return new MemberDTO(memberId, snsType, snsId);
    }

    /**
     * refresh 토큰을 MemberDTO로 변환
     * @throws IllegalArgumentException refresh 토큰이 아닐시
     */
    public MemberDTO pasreRefreshToMemberDTO(String refreshToken){
        refreshToken = removeTokenPrefix(refreshToken);

        if(!equalsTokenType(refreshToken, TokenType.REFRESH)){
            throw new IllegalArgumentException("refresh 토큰이 아님");
        }

        Claims claims = Jwts.parser().setSigningKey(jwtSecretKey)
                .parseClaimsJws(refreshToken)
                .getBody();

        // 토큰의 값 조회
        String snsId = claims.get("snsId", String.class);

        SnsType snsType = null;
        try {
            snsType = SnsType.valueOf(claims.get("snsType", String.class));
        } catch (IllegalArgumentException e){
            throw new AuthorizationException(ErrorCode.TOKEN_TYPE_ILLEGAL);
        }

        return new MemberDTO(null, snsType, snsId);
    }

    private String getRefreshTokenId(String refreshToken){
        return Jwts.parser().setSigningKey(jwtSecretKey)
                .parseClaimsJws(refreshToken)
                .getBody()
                .getId();
    }

    /**
     * refreshToken으로 Member 찾기
     * @throws IllegalArgumentException refresh 토큰이 아닐시
     */
    private Member findMemberByRefreshToken(String refreshToken){
        refreshToken = removeTokenPrefix(refreshToken);

        // refreshToken인지 확인하기
        if(!equalsTokenType(refreshToken, TokenType.REFRESH)){
            throw new IllegalArgumentException("refresh 토큰이 아님");
        }

        Claims claims = Jwts.parser().setSigningKey(jwtSecretKey)
                .parseClaimsJws(refreshToken)
                .getBody();

        String snsId = claims.get("snsId", String.class);
        SnsType snsType;
        try {
            snsType = SnsType.valueOf(claims.get("snsType", String.class));
        } catch (IllegalArgumentException e){
            throw new AuthorizationException(ErrorCode.TOKEN_TYPE_ILLEGAL);
        }

        return memberQueryService.findMember(snsId, snsType);
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
