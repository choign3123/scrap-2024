package com.example.scrap.web.oauth;

import com.example.scrap.web.oauth.dto.CommonOauthMemberInfo;

public interface IOauthMemberInfoProvider {

    CommonOauthMemberInfo getMemberId(String authorization);
}
