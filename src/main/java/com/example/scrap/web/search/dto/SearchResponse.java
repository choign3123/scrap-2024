package com.example.scrap.web.search.dto;

import com.example.scrap.web.baseDTO.Meta;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class SearchResponse {

    /**
     * 스크랩 검색하기 DTO
     */
    @Builder
    @Getter
    public static class FindScrapDTO {

        private Meta meta;
        private List<ScrapDTO> scrapDTOList;

        @Builder
        @Getter
        public static class ScrapDTO{
            private Long scrapId;
            private String categoryTitle;
            private String scrapTitle;
            private String scrapURL;
            private String imageURL;
            private Boolean isFavorite;
            private LocalDate scrapDate;
        }
    }
}
