package com.example.scrap.web.scrap;

import com.example.scrap.entity.Scrap;
import com.example.scrap.web.baseDTO.Sort;
import com.example.scrap.web.member.MemberDTO;
import com.example.scrap.web.scrap.dto.ScrapRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    /**
     * 스크랩 전체 조회 - 카테고리별
     * @param memberDTO
     * @param categoryId
     * @param pageRequest
     * @return
     */
    public Page<Scrap> getScrapListByCategory(MemberDTO memberDTO, Long categoryId, PageRequest pageRequest);

    /**
     * 스크랩 세부 조회
     * @param memberDTO
     * @param scrapId
     * @return
     */
    public Scrap getScrapDetails(MemberDTO memberDTO, Long scrapId);
}
