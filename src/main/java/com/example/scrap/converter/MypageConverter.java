package com.example.scrap.converter;

import com.example.scrap.entity.Member;
import com.example.scrap.web.mypage.dto.MypageResponse;

public class MypageConverter {

    public static MypageResponse.MypageDTO toMypage(Member member, MypageResponse.MypageDTO.Statistics statistics){
        return MypageResponse.MypageDTO.builder()
                .memberInfo(
                        MypageResponse.MypageDTO.MemberInfo.builder()
                                .name(member.getName())
                                .build()
                )
                .statistics(statistics)
                .build();
    }
}
