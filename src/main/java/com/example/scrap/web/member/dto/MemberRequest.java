package com.example.scrap.web.member.dto;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;

public class MemberRequest {

    @Getter
    public static class ValidateTokenDTO {

        @NotEmpty
        private String accessToken;

        @NotEmpty
        private String refreshToken;
    }
}
