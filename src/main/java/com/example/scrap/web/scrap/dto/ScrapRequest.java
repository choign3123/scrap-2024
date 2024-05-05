package com.example.scrap.web.scrap.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

public class ScrapRequest {

    @Getter
    public static class CreateScrap {
        @NotBlank
        private String scrapURL;

        private String imageURL;

        @NotBlank
        private String title;

        private String description;

        private String memo;
    }
}
