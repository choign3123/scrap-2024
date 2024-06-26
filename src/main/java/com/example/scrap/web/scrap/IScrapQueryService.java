package com.example.scrap.web.scrap;

import com.example.scrap.base.exception.BaseException;
import com.example.scrap.base.exception.ValidationException;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.base.enums.PressSelectionType;
import com.example.scrap.web.member.dto.MemberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface IScrapQueryService {

    /**
     * 스크랩 전체 조회 - 카테고리별
     * @param memberDTO
     * @param categoryId
     * @param pageRequest
     * @return
     */
    public Page<Scrap> getScrapListByCategory(MemberDTO memberDTO, Long categoryId, PageRequest pageRequest);

    /**
     * 즐겨찾기된 스크랩 조회
     * @param memberDTO
     * @param pageRequest
     * @return 즐겨찾기된 스크랩
     */
    public Page<Scrap> getFavoriteScrapList(MemberDTO memberDTO, PageRequest pageRequest);

    /**
     * 스크랩 세부 조회
     * @param memberDTO
     * @param scrapId
     * @return
     */
    public Scrap getScrapDetails(MemberDTO memberDTO, Long scrapId);

    /**
     * 스크랩 제목으로 검색 - 카테고리별
     * @param memberDTO
     * @param categoryId
     * @param query
     * @param sort
     * @return
     */
    public List<Scrap> findScrapByTitle(MemberDTO memberDTO, Long categoryId, String query, Sort sort);

    /**
     * 스크랩 전체 공유하기
     * @param memberDTO
     * @param pressSelectionType
     * @param categoryId
     * @return
     */
    public List<Scrap> shareAllScrap(MemberDTO memberDTO, PressSelectionType pressSelectionType, Long categoryId);

    /**
     * 스크랩 찾기
     * @param scrapId
     * @return
     */
    public Scrap findScrap(Long scrapId);

    /**
     * 프레스 타입에 따른 스크랩 조회
     * @param member
     * @param pressSelectionType
     * @param categoryId
     * @return
     * @throws BaseException CATEGORY_MEMBER_NOT_MATCH_IN_SCRAP
     */
    List<Scrap> findAllByPressSelection(Member member, PressSelectionType pressSelectionType, Long categoryId);

    /**
     * 요청된 스크랩 조회
     * @param scrapIdList
     * @param member
     * @return
     * @throws ValidationException if scrapIdList empty
     */
    public List<Scrap> findAllByRequest(List<Long> scrapIdList, Member member);
}
