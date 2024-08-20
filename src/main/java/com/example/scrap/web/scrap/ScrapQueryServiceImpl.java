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
        Category category = categoryQueryService.findCategory(categoryId);

        category.checkIllegalMember(member);

        Specification<Scrap> spec = Specification.where(ScrapSpecification.isAvailable())
                .and(ScrapSpecification.equalMember(member))
                .and(ScrapSpecification.equalCategory(category));

        return  scrapRepository.findAll(spec, pageRequest);
    }

    /**
     * 즐겨찾기된 스크랩 조회
     * @return 즐겨찾기된 스크랩
     */
    public Page<Scrap> getFavoriteScrapList(MemberDTO memberDTO, PageRequest pageRequest){
        Member member = memberQueryService.findMember(memberDTO);

        Specification<Scrap> spec = Specification.where(ScrapSpecification.isAvailable())
                .and(ScrapSpecification.equalMember(member))
                .and(ScrapSpecification.isFavorite());

        return scrapRepository.findAll(spec, pageRequest);
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

        Specification<Scrap> spec = createSpecByQueryRange(member, queryRange, categoryId);

        // 제목으로 검색
        spec = spec.and(ScrapSpecification.containingTitle(query));

        return scrapRepository.findAll(spec, sort);
    }

    /**
     * 스크랩 전체 공유하기
     */
    public List<Scrap> shareAllScrap(MemberDTO memberDTO, QueryRange queryRange, Long categoryId){

        Member member = memberQueryService.findMember(memberDTO);

        return findAllByQueryRange(member, queryRange, categoryId);
    }

    /**
     * 스크랩 찾기
     */
    public Scrap findScrap(Long scrapId){
        return scrapRepository.findById(scrapId).get();
    }

    /**
     * 조회 타입에 따른 스크랩 조회
     * @throws BaseException CATEGORY_MEMBER_NOT_MATCH_IN_SCRAP
     */
    public List<Scrap> findAllByQueryRange(Member member, QueryRange queryRange, Long categoryId){
        Specification<Scrap> spec = createSpecByQueryRange(member, queryRange, categoryId);

        return scrapRepository.findAll(spec);
    }

    /**
     * 조회 타입에 따른 스크랩 Specification 생성
     * @throws BaseException CATEGORY_MEMBER_NOT_MATCH_IN_SCRAP
     */
    public Specification<Scrap> createSpecByQueryRange(Member member, QueryRange queryRange, Long categoryId){
        Specification<Scrap> spec = Specification.where(ScrapSpecification.isAvailable())
                .and(ScrapSpecification.equalMember(member));

        // 어떤 프레스 타입인지
        switch (queryRange){
            case CATEGORY -> {
                Category category = categoryQueryService.findCategory(categoryId);
                // TODO: checkIllegalMember()로 변경하기
                if(category.isIllegalMember(member)){
                    throw new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH_IN_SCRAP);
                }
                spec = spec.and(ScrapSpecification.equalCategory(category));
            }
            case FAVORITE -> {
                spec = spec.and(ScrapSpecification.isFavorite());
            }
        }

        return spec;
    }

    /**
     * 요청된 스크랩 조회
     * @throws IllegalArgumentException if scrapIdList empty
     */
    public List<Scrap> findAllByRequest(List<Long> scrapIdList, Member member){
        List<Scrap> scrapList = new ArrayList<>();

        if(scrapIdList.isEmpty()){
            throw new IllegalArgumentException("scrapIdList가 비어있습니다.");
        }
        for(Long scrapId : scrapIdList){
            Scrap scrap = findScrap(scrapId);

            scrap.checkIllegalMember(member);

            scrapList.add(scrap);
        }

        return scrapList;
    }
}
