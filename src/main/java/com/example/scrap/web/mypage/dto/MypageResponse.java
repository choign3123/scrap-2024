package com.example.scrap.web.mypage.dto;

import lombok.Builder;
import lombok.Getter;

public class MypageResponse {

    @Getter
    public static class MypageDTO {

        private MemberInfo memberInfo;
        private Statistics statistics;

        @Builder
        @Getter
        public static class MemberInfo{
            private String name;
        }

        @Getter
        public static class Statistics{
            private long totalCategory;
            private long totalScrap;

            @Builder
            public Statistics(long totalCategory, long totalScrap) {
                this.totalCategory = totalCategory;
                this.totalScrap = totalScrap;
            }
        }

        @Builder
        public MypageDTO(MemberInfo memberInfo, Statistics statistics) {
            this.memberInfo = memberInfo;
            this.statistics = statistics;
        }
    }
}
