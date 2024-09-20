package com.example.scrap.web.oauth;

import com.example.scrap.entity.enums.SnsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OauthMemberInfoFactory {

    private final KakaoMemberInfoProvider kakaoMemberInfoProvider;
    private final NaverProfileIntoProvider naverProfileIntoProvider;

    public IOauthMemberInfoProvider getOauthMemberInfoProvider(SnsType snsType){
        switch (snsType){
            case NAVER -> {
                return naverProfileIntoProvider;
            }
            case KAKAO -> {
                return kakaoMemberInfoProvider;
            }
            default -> throw new IllegalArgumentException("잘못된 snsType");
        }
    }
}
