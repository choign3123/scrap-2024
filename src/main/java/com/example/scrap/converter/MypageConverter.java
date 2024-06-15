package com.example.scrap.converter;

import com.example.scrap.web.mypage.dto.MypageResponse;

public class MypageConverter {

    public static MypageResponse.MypageDTO toMypage(int totalScrap){
        return MypageResponse.MypageDTO.builder()
                .totalScrap(totalScrap)
                .build();
    }
}
