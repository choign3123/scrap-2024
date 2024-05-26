package com.example.scrap.web.mypage.dto;

import lombok.Builder;
import lombok.Getter;

public class MypageResponse {

    @Builder
    @Getter
    public static class Mypage{
        private int totalScrap;
    }
}
