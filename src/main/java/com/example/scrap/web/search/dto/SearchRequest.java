package com.example.scrap.web.search.dto;

import com.example.scrap.validation.annotaion.EnumsValid;
import com.example.scrap.base.data.DefaultData;
import com.example.scrap.base.enums.SearchScopeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class SearchRequest {

    /**
     * 스크랩 검색하기 DTO
     */
    @Getter
    @NoArgsConstructor
    public static class FindScrapDTO {

        @EnumsValid(enumC = SearchScopeType.class)
        private List<String> searchScope;

        private List<Long> categoryScope;

        private LocalDate startDate;

        private LocalDate endDate;

        public void setStartDateToDefault(){
            startDate = DefaultData.START_DATE;
        }

        public void setEndDateToDefault(){
            endDate = LocalDate.now();
        }
    }
}
