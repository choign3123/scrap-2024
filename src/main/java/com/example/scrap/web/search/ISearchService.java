package com.example.scrap.web.search;

import com.example.scrap.entity.Scrap;
import com.example.scrap.web.member.MemberDTO;
import com.example.scrap.web.search.dto.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ISearchService {

    /**
     * 스크랩 검색
     * @param memberDTO
     * @param request
     * @param pageRequest
     * @param query
     * @return
     */
    public Page<Scrap> findScrap(MemberDTO memberDTO, SearchRequest.FindScrapDTO request, PageRequest pageRequest, String query);
}
