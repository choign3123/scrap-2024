package com.example.scrap.web.scrap.dto;

import com.example.scrap.validation.annotaion.ExistAvailableScraps;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ScrapRequest {

    /**
     * 스크랩 생성
     */
    @Getter
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
    public static class UpdateScrapMemoDTO {
        @NotNull
        private String memo;
    }

    /**
     * 스크랩 삭제(목록)
     */
    @Getter
    public static class DeleteScrapListDTO {

        @ExistAvailableScraps
        @JsonProperty("scraps")
        private List<Long> scrapIdList;
    }

    /**
     * 스크랩 즐겨찾기(목록)
     */
    @Getter
    public static class ToggleScrapFavoriteListDTO {

        @ExistAvailableScraps
        @JsonProperty("scraps")
        private List<Long> scrapIdList;
    }
}
