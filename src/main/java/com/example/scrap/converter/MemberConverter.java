package com.example.scrap.converter;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.MemberLog;
import com.example.scrap.entity.enums.LoginStatus;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.oauth.dto.NaverResponse;

import java.time.LocalDateTime;

public class MemberConverter {

    public static Member toEntity(NaverResponse.ProfileInfo.Response profileInfo, SnsType snsType, MemberLog memberLog){

        return Member.builder()
                .name(profileInfo.getName())
                .snsType(snsType)
                .snsId(profileInfo.getId())
                .memberLog(memberLog)
                .build();
    }
}
