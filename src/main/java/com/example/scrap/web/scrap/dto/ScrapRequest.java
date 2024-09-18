package com.example.scrap.web.scrap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(example = "https://www.youtube.com/watch?v=gKD8GzCcFwk")
        @NotBlank
        private String scrapURL;

        @Schema(example = "https://i.ytimg.com/vi/gKD8GzCcFwk/maxresdefault.jpg")
        private String imageURL;

        @Schema(example = "포동포동 겨울참새는 아주 귀엽습니다  (밀착 관찰)")
        @NotBlank
        private String title;

        @Schema(example = "귀여운 참새")
        private String description;

        @Schema(example = "너무 귀여운 겨울 참새")
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
