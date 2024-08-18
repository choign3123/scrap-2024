package com.example.scrap.web.search;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.specification.ScrapSpecification;
import com.example.scrap.base.enums.SearchScopeType;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.scrap.ScrapRepository;
import com.example.scrap.web.search.dto.SearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SearchQueryService implements ISearchQueryService {

    private final IMemberQueryService memberService;
    private final ScrapRepository scrapRepository;

    /**
     * 스크랩 검색
     * @param memberDTO
     * @param request
     * @param pageRequest
     * @param query
     * @return
     */
    public Page<Scrap> findScrap(MemberDTO memberDTO, SearchRequest.FindScrapDTO request, PageRequest pageRequest, String query){

        Member member = memberService.findMember(memberDTO);

        Specification<Scrap> spec = Specification.where(ScrapSpecification.isAvailable())
                .and(ScrapSpecification.equalMember(member));


        // 검색범위 지정
        Specification<Scrap> specSearchScope = (root, q, criteriaBuilder) -> null;
        for(String searchType : request.getSearchScope()){
            specSearchScope = specSearchScope.or(ScrapSpecification.containingQueryInSearchType(query,
                    SearchScopeType.valueOf(searchType.toUpperCase())));
        }
        spec = spec.and(specSearchScope);

        // 카테고리 범위 지정
        if(!(request.getCategoryIdList() == null || request.getCategoryIdList().isEmpty())){
            spec = spec.and(ScrapSpecification.inCategory(request.getCategoryIdList()));
        }

        // 시작 날짜 ~ 종료 날짜 지정
        spec = spec.and(ScrapSpecification.betweenScrapDate(request.getStartDate(), request.getEndDate()));

        return scrapRepository.findAll(spec, pageRequest);
    }
}
