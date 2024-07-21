package com.example.scrap.web.member.dto;

import lombok.Builder;
import lombok.Getter;

public class MemberResponse {

    @Getter
    @Builder
    public static class ValidateTokenDTO {
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Builder
    public static class ReissueTokenDTO {
        private String accessToken;
        private String refreshToken;
    }
}
