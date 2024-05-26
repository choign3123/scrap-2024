package com.example.scrap.converter;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.web.mypage.dto.MypageResponse;

import java.util.List;

public class MypageConverter {

    public static MypageResponse.Mypage toMypage(int totalScrap){
        return MypageResponse.Mypage.builder()
                .totalScrap(totalScrap)
                .build();
    }
}
