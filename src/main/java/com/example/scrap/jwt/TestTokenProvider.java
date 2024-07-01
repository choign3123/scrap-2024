package com.example.scrap.jwt;

import com.example.scrap.base.data.DefaultData;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.dto.TokenType;
import com.example.scrap.web.member.dto.MemberDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class TestTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.reissue_time.access}")
    private int hourOfRequiredReissueAccessToken;

    @Value("${jwt.reissue_time.refresh}")
    private int dayOfRequiredReissueRefreshToken;

    private final TokenProvider tokenProvider;

    /* 토큰 발급 **/
    /**
     * 테스트 토큰 발급하기
     */
    public Map<String, String> createTestToken(@Nullable MemberDTO memberDTO){
        if(memberDTO == null){
            memberDTO = new MemberDTO(1L, SnsType.NAVER, "temp1234");
        }

        Map<String, String> map = new HashMap<>();

        long reissueHourOfAccess = pareHourToMs(hourOfRequiredReissueAccessToken) - pareMinuteToMs(30);
        map.put("유효한 accessToken", createToken(memberDTO, parseDayToMs(10), TokenType.ACCESS));
        map.put("유효한 accessToken (갱신 필요함)", createToken(memberDTO, reissueHourOfAccess, TokenType.ACCESS));

        long reissueDayOfRefresh = parseDayToMs(dayOfRequiredReissueRefreshToken) - pareMinuteToMs(30);
        map.put("유효한 refreshToken", createToken(memberDTO, parseDayToMs(10), TokenType.REFRESH));
        map.put("유효한 refreshToken (갱신 필요함)", createToken(memberDTO, reissueDayOfRefresh, TokenType.REFRESH));

        map.put("유효하지 않은 accessToken", createToken(memberDTO, 0, TokenType.ACCESS));
        map.put("유효하지 않은 refreshToken", createToken(memberDTO, 0, TokenType.REFRESH));

        return map;
    }

    /**
     * 토큰 생성하기
     */
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
     * 토큰 유효성 검사.
     * @return if token is valid, return true. else return false.
     */
    public boolean isTokenValid(String token){
        return tokenProvider.isTokenValid(token);
    }

    public boolean isRequiredTokenReissue(String token){
        return tokenProvider.isRequiredTokenReissue(token);
    }

    public Date getTokenExpireDate(String token){
        token = token.replace(DefaultData.AUTH_PREFIX, "");

        return Jwts.parser().setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
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

    /**
     * minute을 밀리초로 변환
     */
    private long pareMinuteToMs(int minute) {
        return minute * 60L * 1000L; // 초, 밀리단위
    }
}
