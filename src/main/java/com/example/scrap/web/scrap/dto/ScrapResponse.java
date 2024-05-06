package com.example.scrap.web.scrap.dto;

import com.example.scrap.web.baseDTO.Meta;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class ScrapResponse {

    @Builder
    @Getter
    public static class CreateScrapDTO{
        private Long scrapId;
        private String title;
        private String scrapURL;
        private String imageURL;
        private Boolean isFavorite;
        private LocalDate scrapDate;
    }

    @Builder
    @Getter
    public static class GetScrapListByCategory {
        private Meta meta;

        @JsonProperty("scraps")
        private List<ScrapDTO> scrapDTOList;

        @Builder
        @Getter
        public static class ScrapDTO{
            private Long scrapId;
            private String title;
            private String scrapURL;
            private String imageURL;
            private Boolean isFavorite;
            private LocalDate scrapDate;
        }
    }

    @Builder
    @Getter
    public static class GetScrapDetails {
        private Long scrapId;
        private String title;
        private String scrapURL;
        private String imageURL;
        private String description;
        private String memo;
        private Boolean isFavorite;
    }
}
