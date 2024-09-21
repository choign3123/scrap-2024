package com.example.scrap.web.oauth.dto;

import lombok.Getter;

public class KakaoResponse {

    @Getter
    public static class MemberInfo{
        private Long id;
        private Properties properties;

        @Getter
        public static class Properties{
            String nickname;
        }
    }
}
