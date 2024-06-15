package com.example.scrap.web.scrap.dto;

import com.example.scrap.web.baseDTO.Meta;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class ScrapResponse {

    /**
     * 스크랪 생성 DTO
     */
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

    /**
     * 스크랩 전체조회 - 카테고리별 DTO
     */
    @Builder
    @Getter
    public static class GetScrapListByCategoryDTO {
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

    /**
     * 즐겨찾기된 스크랩 조회
     */
    @Builder
    @Getter
    public static class GetFavoriteScrapListDTO {
        private Meta meta;

        @JsonProperty("scraps")
        private List<ScrapDTO> scrapDTOList;

        @Builder
        @Getter
        public static class ScrapDTO{
            private String categoryTitle;
            private Long scrapId;
            private String scrapTitle;
            private String scrapURL;
            private String imageURL;
            private LocalDate scrapDate;
        }
    }

    /**
     * 스크랩 세부조회 DTO
     */
    @Builder
    @Getter
    public static class GetScrapDetailsDTO {
        private Long scrapId;
        private String title;
        private String scrapURL;
        private String imageURL;
        private String description;
        private String memo;
        private Boolean isFavorite;
    }

    /**
     * 스크랩 제목으로 검색 - 카테고리별
     */
    @Builder
    @Getter
    public static class FindScrapByTitleDTO {

        @JsonProperty("scraps")
        private List<ScrapDTO> scrapDTOList;

        private int total;

        @Builder
        @Getter
        public static class ScrapDTO {
            private Long scrapId;
            private String title;
            private String scrapURL;
            private String imageURL;
            private Boolean isFavorite;
            private LocalDate scrapDate;
        }
    }

    /**
     * 스크랩 전체 공유하기
     */
    @Builder
    @Getter
    public static class ShareAllScrapDTO {

        private int total;

        @JsonProperty("scraps")
        private List<ScrapDTO> scrapDTOList;

        @Builder
        @Getter
        public static class ScrapDTO {
            private Long scrapId;
            private String title;
            private String scrapURL;
        }
    }

    /**
     * 스크랩 즐겨찾기(단건)
     */
    @Builder
    @Getter
    public static class ToggleScrapFavoriteDTO {

        private Long scrapId;
        private Boolean isFavorite;
    }

    /**
     * 스크랩 즐겨찾기(목록)
     */
    @Builder
    @Getter
    public static class ToggleScrapFavoriteListDTO {

        private int total;

        @JsonProperty("scraps")
        private List<ScrapDTO> scrapDTOList;

        @Builder
        @Getter
        public static class ScrapDTO {
            private Long scrapId;
            private Boolean isFavorite;
        }
    }

    /**
     * 스크랩의 메모 수정
     */
    @Builder
    @Getter
    public static class UpdateScrapMemoDTO {

        private Long scrapId;
        private String memo;
    }

    /**
     * 스크랩 이동하기 (단건)
     */
    @Builder
    @Getter
    public static class MoveCategoryOfScrapDTO {
        private Long scrapId;
        private Long categoryId;
    }

    /**
     * 스크랩 이동하기 (목록)
     */
    @Builder
    @Getter
    public static class MoveCategoryOfScrapListDTO {

        private int total;

        @JsonProperty("scraps")
        private List<ScrapDTO> scrapDTOList;

        @Getter
        @Builder
        public static class ScrapDTO{
            private Long scrapId;
            private Long categoryId;
        }
    }
}
