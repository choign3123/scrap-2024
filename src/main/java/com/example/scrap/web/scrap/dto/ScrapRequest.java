package com.example.scrap.web.scrap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ScrapRequest {

    /**
     * 스크랩 생성
     */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateScrapDTO {
        @NotBlank
        private String scrapURL;

        private String imageURL;

        @NotBlank
        private String title;

        private String description;

        private String memo;

        private Boolean isFavorite;
    }

    /**
     * 스크랩의 메모 수정
     */
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateScrapMemoDTO {
        @NotNull
        private String memo;
    }

    /**
     * 스크랩 삭제(목록)
     */
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeleteScrapListDTO {

        @NotEmpty
        private List<Long> scrapIdList;
    }

    /**
     * 스크랩 즐겨찾기(목록)
     */
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ToggleScrapFavoriteListDTO {

        @NotEmpty
        private List<Long> scrapIdList;
    }

    /**
     * 스크랩 이동하기 (단건)
     */
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MoveCategoryOfScrapDTO {

        @NotNull
        private Long moveCategoryId;
    }

    /**
     * 스크랩 이동하기 (목록)
     */
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MoveCategoryOfScrapsDTO {

        @NotEmpty
        private List<Long> scrapIdList;

        @NotNull
        private Long moveCategoryId;
    }
}
