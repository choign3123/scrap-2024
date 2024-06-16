package com.example.scrap.jwt;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.dto.Token;
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


    /**
     * 토큰 발급하기
     * @param snsType
     * @param snsId
     * @return
     */
    public Token createToken(SnsType snsType, String snsId){
        Claims claims = Jwts.claims();
        claims.put("snsType", snsType);
        claims.put("snsId", snsId);

        long currentTimeMills = System.currentTimeMillis();

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTimeMills))
                .setExpiration(new Date(currentTimeMills+ parseDayToMs(expireDayOfAccessToken)))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTimeMills))
                .setExpiration(new Date(currentTimeMills + parseDayToMs(expireDayOfRefreshToken)))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 토큰 발급하기
     * @param snsType
     * @param snsId
     * @param expireDay
     * @return
     */
    private String createToken(SnsType snsType, String snsId, int expireDay){
        Claims claims = Jwts.claims();
        claims.put("snsType", snsType);
        claims.put("snsId", snsId);

        long currentTimeMills = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTimeMills))
                .setExpiration(new Date(currentTimeMills+ parseDayToMs(expireDay)))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .compact();
    }

    /**
     * access 토큰 재발급하기
     * @param token
     * @return
     * @throws AuthorizationException accessToken과 refreshToken의 member가 다르면
     */
    public Token reissueAccessToken(Token token){

        // accessToken과 refreshToken의 snsType과 id 같은지 확인
        if(!checkMemberOfTokenSame(token)){
            throw new AuthorizationException(ErrorCode._REQUIRED_RE_LOGIN);
        }

        MemberDTO memberDTO = parseMemberDTO(token.getAccessToken());
        String reissuedAccessToken = createToken(memberDTO.getSnsType(), memberDTO.getSnsId(), expireDayOfAccessToken);

        return Token.builder()
                .accessToken(reissuedAccessToken)
                .refreshToken(token.getRefreshToken())
                .build();
    }

    /**
     * refresh 토큰 재발급하기
     * @param token
     * @return
     * @throws AuthorizationException accessToken과 refreshToken의 member가 다르면
     */
    public Token reissueRefreshToken(Token token){

        // accessToken과 refreshToken의 snsType과 id 같은지 확인
        if(!checkMemberOfTokenSame(token)){
            throw new BaseException(ErrorCode._REQUIRED_RE_LOGIN);
        }

        MemberDTO memberDTO = parseMemberDTO(token.getRefreshToken());
        String reissuedRefreshToken = createToken(memberDTO.getSnsType(), memberDTO.getSnsId(), expireDayOfRefreshToken);

        return Token.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(reissuedRefreshToken)
                .build();
    }

    /**
     * access토큰과 refresh토큰이 동일한 유저의 것인지 확인
     * @param token
     * @return if member of accessToken and member of refreshToken is same, return true. else return false.
     */
    public boolean checkMemberOfTokenSame(Token token){

        MemberDTO accessOfMemberDTO = parseMemberDTO(token.getAccessToken());
        MemberDTO refreshOfMemberDTO = parseMemberDTO(token.getAccessToken());

        return accessOfMemberDTO.equals(refreshOfMemberDTO);
    }

    /**
     * 토큰 유효성 검사.
     * @param token
     * @return if token expire or fail to parse, return false. else return ture.
     */
    public boolean validateToken(String token){
        try {
            return Jwts.parser().setSigningKey(jwtSecretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());

        } catch (Exception ex) {
            log.info("토큰 유효성 검사 실패: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * 토큰을 MemberDTO로 변환
     * @param token
     * @return
     * @throws AuthorizationException 유효성 검증 실패시 ErrorCode._REQUIRED_RE_LOGIN 에러 던짐
     */
    public MemberDTO parseMemberDTO(String token){
        if(!validateToken(token)){
            throw new AuthorizationException(ErrorCode._REQUIRED_RE_LOGIN);
        }

        Claims claims = Jwts.parser().setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody();

        SnsType snsType = claims.get("snsType", SnsType.class);
        String snsId = claims.get("snsId", String.class);

        return new MemberDTO(snsType, snsId);
    }

    /**
     * day를 밀리초로 변환
     * @param day
     * @return
     */
    private long parseDayToMs(int day){
        return day * 60L * 60L * 1000L; // 초, 분, 밀리단위
    }
}
