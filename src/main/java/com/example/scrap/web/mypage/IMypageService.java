package com.example.scrap.web.mypage;

import com.example.scrap.web.member.MemberDTO;
import com.example.scrap.web.mypage.dto.MypageResponse;

public interface IMypageService {

    /**
     * 마이페이지 조회
     * @param memberDTO
     * @return
     */
    public MypageResponse.MypageDTO mypage(MemberDTO memberDTO);

}
