package com.example.scrap.web.scrap;

import com.example.scrap.base.exception.BaseException;
import com.example.scrap.base.exception.ValidationException;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.base.enums.QueryRange;
import com.example.scrap.web.member.dto.MemberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface IScrapQueryService {

    /**
     * 스크랩 전체 조회 - 카테고리별
     */
    public Page<Scrap> getScrapListByCategory(MemberDTO memberDTO, Long categoryId, PageRequest pageRequest);

    /**
     * 즐겨찾기된 스크랩 조회
     */
    public Page<Scrap> getFavoriteScrapList(MemberDTO memberDTO, PageRequest pageRequest);

    /**
     * 스크랩 세부 조회
     */
    public Scrap getScrapDetails(MemberDTO memberDTO, Long scrapId);

    /**
     * 스크랩 제목으로 검색 - 카테고리별
     */
    public List<Scrap> findScrapByTitle(MemberDTO memberDTO, QueryRange queryRange, Long categoryId, String query, Sort sort);

    /**
     * 스크랩 전체 공유하기
     */
    public List<Scrap> shareAllScrap(MemberDTO memberDTO, QueryRange queryRange, Long categoryId);

    /**
     * 스크랩 찾기
     *
     * @throws BaseException 해당하는 스크랩이 존재하지 않을 경우
     */
    public Scrap findScrap(Long scrapId);

    /**
     * 스크랩 찾기
     *
     * @throws BaseException 리스트 중 존재하지 않는 스크랩이 있을 경우
     */
    List<Scrap> findScrapList(List<Long> scrapIdList);

    // TODO: 스크랩 조회시 member 일치 여부도 함께 확인하는 메소드 추가하기
}
