package com.example.scrap.web.scrap;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.base.exception.ValidationException;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.specification.ScrapSpecification;
import com.example.scrap.base.enums.QueryRange;
import com.example.scrap.web.category.ICategoryQueryService;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ScrapQueryServiceImpl implements IScrapQueryService {

    private final IMemberQueryService memberQueryService;
    private final ICategoryQueryService categoryQueryService;
    private final ScrapRepository scrapRepository;

    /**
     * 스크랩 전체 조회 - 카테고리별
     */
    public Page<Scrap> getScrapListByCategory(MemberDTO memberDTO, Long categoryId, PageRequest pageRequest){
        Member member = memberQueryService.findMember(memberDTO);
        Category category = categoryQueryService.findCategory(categoryId, member);

        return scrapRepository.findByMemberAndCategory(member, category, pageRequest);
    }

    /**
     * 즐겨찾기된 스크랩 조회
     * @return 즐겨찾기된 스크랩
     */
    public Page<Scrap> getFavoriteScrapList(MemberDTO memberDTO, PageRequest pageRequest){
        Member member = memberQueryService.findMember(memberDTO);

        return scrapRepository.findByMemberAndIsFavoriteIsTrue(member, pageRequest);
    }

    /**
     * 스크랩 세부 조회
     */
    public Scrap getScrapDetails(MemberDTO memberDTO, Long scrapId){
        Member member = memberQueryService.findMember(memberDTO);

        Scrap scrap = findScrap(scrapId);

        scrap.checkIllegalMember(member);

        return scrap;
    }

    /**
     * 스크랩 제목으로 검색
     * @param query 제목
     */
    public List<Scrap> findScrapByTitle(MemberDTO memberDTO, QueryRange queryRange, Long categoryId, String query, Sort sort){
        Member member = memberQueryService.findMember(memberDTO);
        Category category = null;

        // 카테고리를 대상으로 조회가 필요한 경우
        if(queryRange.equals(QueryRange.CATEGORY)){
            category = categoryQueryService.findCategory(categoryId, member);
        }

        // 동적인 쿼리 생성
        Specification<Scrap> spec = Specification.where(ScrapSpecification.equalMember(member))
                .and(ScrapSpecification.containingTitle(query)); // 제목 조건
        spec = addQueryRangeSpec(spec, queryRange, category);

        return scrapRepository.findAll(spec, sort);
    }

    /**
     * 스크랩 전체 공유하기
     */
    public List<Scrap> shareAllScrap(MemberDTO memberDTO, QueryRange queryRange, Long categoryId){
        Member member = memberQueryService.findMember(memberDTO);
        Category category = null;

        // 카테고리를 대상으로 조회가 필요한 경우
        if(queryRange.equals(QueryRange.CATEGORY)){
            category = categoryQueryService.findCategory(categoryId, member);
        }

        // 동적인 쿼리 생성
        Specification<Scrap> spec = Specification.where(ScrapSpecification.equalMember(member));
        spec = addQueryRangeSpec(spec, queryRange, category);

        return scrapRepository.findAll(spec);
    }

    /**
     * 스크랩 찾기
     *
     * @throws BaseException 해당하는 스크랩이 존재하지 않을 경우
     */
    @Override
    public Scrap findScrap(Long scrapId){
        return scrapRepository.findById(scrapId)
                .orElseThrow(() -> new BaseException(ErrorCode.SCRAP_NOT_FOUND));
    }

    /**
     * QueryRange에 따른 Specification 추가
     */
    private Specification<Scrap> addQueryRangeSpec(Specification<Scrap> spec, QueryRange queryRange, @Nullable Category category){
        switch (queryRange){
            case CATEGORY -> { // 카테고리에서 검색
                spec = spec.and(ScrapSpecification.equalCategory(category));
            }
            case FAVORITE -> { // 즐겨찾기에서 검색
                spec = spec.and(ScrapSpecification.isFavorite());
            }
        }

        return spec;
    }
}
