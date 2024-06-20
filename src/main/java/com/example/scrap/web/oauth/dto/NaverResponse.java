package com.example.scrap.web.oauth.dto;

import lombok.Getter;

public class NaverResponse {

    @Getter
    public static class ProfileInfo {
        private String resultcode;
        private String message;
        private Response response;

        @Getter
        public static class Response {
            private String id;
            private String name;
        }
    }
}
