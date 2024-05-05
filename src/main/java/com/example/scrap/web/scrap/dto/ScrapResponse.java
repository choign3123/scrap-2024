package com.example.scrap.web.scrap.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

public class ScrapResponse {

    @Builder
    @Getter
    public static class CreateScrapDTO{
        private Long scrapId;
        private String title;
        private String scrapURL;
        private String imageURL;
        private Boolean isStar;
        private LocalDate scrapDate;
    }
}
