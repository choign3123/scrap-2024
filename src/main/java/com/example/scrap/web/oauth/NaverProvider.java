package com.example.scrap.web.oauth;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.web.oauth.dto.NaverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class NaverProvider {

    private final RestTemplate restTemplate;

    // 접근 토큰을 이용하여 프로필 API 호출하기
    /**
     * TODO: 다른 외부 API를 호출할 수 있는 방법에 대해서도 고민해보기
     * https://jie0025.tistory.com/531
     * 지금은 외부 API를 호출하는 시점이 소셜 로그인을 할 때 뿐이라, 구현이 복잡하지 않은 기술을 택.
     */
    public NaverResponse.ProfileInfo getProfileByAccessToken(String authorization){
        String url = "https://openapi.naver.com/v1/nid/me";
        authorization = "Bearer " + authorization;

        // header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authorization);

        // request entity 설정
        HttpEntity<Void> entity = new HttpEntity<>(headers);


        // API 호출
        try{
            ResponseEntity<NaverResponse.ProfileInfo> response = restTemplate.exchange(url, HttpMethod.GET, entity, NaverResponse.ProfileInfo.class);
            return response.getBody();
        }
        catch (HttpClientErrorException e){
            throw new AuthorizationException(ErrorCode.OAUTH_NAVER_LOGIN_FAIL);
        }
    }
}
