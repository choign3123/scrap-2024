package com.example.scrap.web.mypage;

import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.mypage.dto.MypageResponse;

public interface IMypageQueryService {

    /**
     * 마이페이지 조회
     * @param memberDTO
     * @return
     */
    public MypageResponse.MypageDTO mypage(MemberDTO memberDTO);

}
