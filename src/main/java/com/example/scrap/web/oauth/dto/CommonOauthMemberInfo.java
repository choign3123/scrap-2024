package com.example.scrap.web.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommonOauthMemberInfo {

    private String snsId;
    private String name;
}
