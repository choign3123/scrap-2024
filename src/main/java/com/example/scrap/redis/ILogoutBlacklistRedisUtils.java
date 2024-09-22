package com.example.scrap.redis;

import com.example.scrap.entity.Member;

public interface ILogoutBlacklistRedisUtils {

    /**
     * 로그아웃한 토큰 블랙리스트에 추가하기
     */
    void addLogoutToken(String token, Member member);

    /**
     * 블랙리스트에 해당 토큰이 있는지 확인하기
     *
     * @return 블랙리스트에 토큰이 존재하면 return true. else return false.
     */
    boolean existToken(String token);
}
