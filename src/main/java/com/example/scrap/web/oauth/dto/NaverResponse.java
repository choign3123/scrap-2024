package com.example.scrap.web.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NaverResponse {

    @Getter
    public static class ProfileInfo {
        private String resultcode;
        private String message;
        private Response response;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Response {
            private String id;
            private String name;
        }
    }
}
