package com.example.scrap.web.scrap;

import com.example.scrap.entity.Scrap;
import com.example.scrap.web.member.MemberDTO;
import com.example.scrap.web.scrap.dto.ScrapRequest;
import org.springframework.transaction.annotation.Transactional;

public interface IScrapService {

    /**
     * 스크랩 생성
     * @param memberDTO
     * @param categoryId
     * @param request
     * @return 생성된 스크랩
     */
    public Scrap createScrap(MemberDTO memberDTO, Long categoryId, ScrapRequest.CreateScrap request);
}
