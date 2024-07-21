package com.example.scrap;

import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.TestTokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Map;

@SpringBootTest
public class TestTokenProviderTests {

    @Autowired
    private TestTokenProvider testTokenProvider;

    @Test
    @DisplayName("테스트 토큰 발급")
    public void createTestToken(){

        Map<String, String> map = testTokenProvider.createTestToken(
                MemberDTO.builder()
                        .memberId(2L)
                        .snsId("test1234")
                        .snsType(SnsType.NAVER)
                        .build()
        );

        for(String key : map.keySet()){
            System.out.printf("%s: %s\n", key, map.get(key));
        }
    }
}
