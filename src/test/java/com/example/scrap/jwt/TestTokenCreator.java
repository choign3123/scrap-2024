package com.example.scrap.jwt;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.MemberLog;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.member.dto.MemberDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

public class TestTokenCreator {

    @DisplayName("테스트 토큰 생성하기")
    @Test
    public void createTestToken(){
        TokenProviderImpl tokenProvider = new TokenProviderImpl();

        // 필요한 필드값 환경변수로부터 주입
        String jwtSecretKey = System.getenv("TEST_JWT_SECRET");
        int expireHourOfAccessToken = Integer.parseInt(System.getenv("TEST_EXPIRE_HOUR_OF_ACCESS"));
        int expireDayOfRefreshToken = Integer.parseInt(System.getenv("TEST_EXPIRE_DAY_OF_REFRESH"));

        ReflectionTestUtils.setField(tokenProvider, "jwtSecretKey", jwtSecretKey);
        ReflectionTestUtils.setField(tokenProvider, "expireHourOfAccessToken", expireHourOfAccessToken);
        ReflectionTestUtils.setField(tokenProvider, "expireDayOfRefreshToken", expireDayOfRefreshToken);

        // Member 설정
        MemberLog memberLog = new MemberLog();
        Member member = Member.builder()
                .snsId("hongTestSnsId") // 여기에 원하는 사용자 값 입력
                .snsType(SnsType.NAVER) // 여기에 원하는 사용자 값 입력
                .memberLog(memberLog)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L); // value에 원하는 사용자 값 입력

        Token token = tokenProvider.createToken(member);
        System.out.printf("accessToken: %s\n", token.getAccessToken());
        System.out.printf("refreshToken: %s", token.getRefreshToken());
    }

    @DisplayName("토큰 유효성 검사")
    @Test
    public void validateToken(){
        TokenProviderImpl tokenProvider = new TokenProviderImpl();

        // 필요한 필드값 환경변수로부터 주입
        String jwtSecretKey = System.getenv("TEST_JWT_SECRET");
        int expireHourOfAccessToken = Integer.parseInt(System.getenv("TEST_EXPIRE_HOUR_OF_ACCESS"));
        int expireDayOfRefreshToken = Integer.parseInt(System.getenv("TEST_EXPIRE_DAY_OF_REFRESH"));

        ReflectionTestUtils.setField(tokenProvider, "jwtSecretKey", jwtSecretKey);
        ReflectionTestUtils.setField(tokenProvider, "expireHourOfAccessToken", expireHourOfAccessToken);
        ReflectionTestUtils.setField(tokenProvider, "expireDayOfRefreshToken", expireDayOfRefreshToken);

        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzbnNUeXBlIjoiTkFWRVIiLCJzbnNJZCI6ImhvbmdUZXN0U25zSWQiLCJ0eXBlIjoiQUNDRVNTIiwiYXVkIjoiMSIsImlhdCI6MTcyNjgxMjE3NCwiZXhwIjoxNzM0NTg4MTc0fQ.-Ek5oMLlP3Qk0tmtJtjxEP5aXI8jijqfl6SMWY-iY74";

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecretKey.getBytes())
                .parseClaimsJws(accessToken)
                .getBody();

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();
        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(accessToken);

        System.out.println("issuedAt: " + issuedAt);
        System.out.println("expiration: " + expiration);
        System.out.println("memberId: " + memberDTO.getMemberId().get());
        System.out.println("snsId: " + memberDTO.getSnsId());
        System.out.println("snsType: " + memberDTO.getSnsType());
    }
}
