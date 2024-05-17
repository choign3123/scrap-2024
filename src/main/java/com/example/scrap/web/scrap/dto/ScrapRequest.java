package com.example.scrap.web.scrap.dto;

import com.example.scrap.validation.annotaion.ExistScraps;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.List;

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

    @Getter
    public static class DeleteScrapList{

        @ExistScraps
        @JsonProperty("scraps")
        private List<Long> scrapIdList;
    }
}
