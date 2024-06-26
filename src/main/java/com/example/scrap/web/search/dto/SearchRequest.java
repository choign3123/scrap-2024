package com.example.scrap.web.search.dto;

import com.example.scrap.validation.annotaion.EnumsValid;
import com.example.scrap.validation.annotaion.ExistCategories;
import com.example.scrap.base.Data;
import com.example.scrap.base.enums.SearchScopeType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class SearchRequest {

    /**
     * 스크랩 검색하기 DTO
     */
    @Getter
    public static class FindScrapDTO {

        @EnumsValid(enumC = SearchScopeType.class)
        private List<String> searchScope;

        @ExistCategories(required = false)
        @JsonProperty(value = "categoryScope")
        private List<Long> categoryIdList;

        private LocalDate startDate;

        private LocalDate endDate;

        public void setStartDateToDefault(){
            startDate = Data.START_DATE;
        }

        public void setEndDateToDefault(){
            endDate = LocalDate.now();
        }
    }
}
