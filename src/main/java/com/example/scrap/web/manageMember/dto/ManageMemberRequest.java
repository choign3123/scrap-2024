package com.example.scrap.web.manageMember.dto;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;

public class ManageMemberRequest {

    @Getter
    public static class ValidateTokenDTO {

        @NotEmpty
        private String accessToken;

        @NotEmpty
        private String refreshToken;
    }
}
