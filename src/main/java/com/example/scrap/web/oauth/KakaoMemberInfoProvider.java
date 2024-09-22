package com.example.scrap.web.oauth;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.web.oauth.dto.CommonOauthMemberInfo;
import com.example.scrap.web.oauth.dto.KakaoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoMemberInfoProvider implements IOauthMemberInfoProvider{

    private final RestTemplate restTemplate;

    @Override
    public CommonOauthMemberInfo getMemberId(String authorization){
        String url = "https://kapi.kakao.com/v2/user/me";
        authorization = "Bearer " + authorization;

        // header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authorization);

        // request entity 설정
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // API 호출
        try{
            ResponseEntity<KakaoResponse.MemberInfo> response = restTemplate.exchange(url, HttpMethod.GET, entity, KakaoResponse.MemberInfo.class);
            KakaoResponse.MemberInfo responseBody = response.getBody();

            return CommonOauthMemberInfo.builder()
                    .snsId(responseBody.getId().toString())
                    .name(responseBody.getProperties().getNickname())
                    .build();
        }
        catch (Exception e){
            throw new AuthorizationException(ErrorCode.OAUTH_KAKAO_LOGIN_FAIL);
        }
    }
}
