package com.example.scrap.web.manageMember.dto;

import lombok.Builder;
import lombok.Getter;

public class ManageMemberResponse {

    @Getter
    @Builder
    public static class ValidateTokenDTO {
        private String accessToken;
        private String refreshToken;
    }
}
