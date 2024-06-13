package com.example.scrap.web.oauth;

import com.example.scrap.web.baseDTO.Token;

// [TODO] 어떻게 네이버와 구글을 구분해서 사용할 수 있을지 고민 필요
public interface IOauthService {

    /**
     * 소셜 로그인
     * @param authorization
     * @return
     */
    public Token login(String authorization);
}
