package com.example.scrap.web.scrap;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.specification.ScrapSpecification;
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
     * 스크랩 검색 (특정 카테고리에서)
     */
    public List<Scrap> findScrapAtParticularCategory(MemberDTO memberDTO, Long categoryId, Sort sort, String query){
        Member member = memberQueryService.findMember(memberDTO);
        Category category = categoryQueryService.findCategory(categoryId, member);

        // 검색 범위 지정 (제목, 본문내용, 메모)
        Specification<Scrap> inquiryRange = Specification.where(ScrapSpecification.containingTitle(query))
                .or(ScrapSpecification.containingDescription(query))
                .or(ScrapSpecification.containingMemo(query));

        // 쿼리 생성
        Specification<Scrap> spec = Specification.where(ScrapSpecification.equalMember(member))
                .and(ScrapSpecification.equalCategory(category))
                .and(inquiryRange);

        return scrapRepository.findAll(spec, sort);
    }

    /**
     * 스크랩 검색 (즐겨찾기됨에서)
     */
    public List<Scrap> findScrapAtFavorite(MemberDTO memberDTO, Sort sort, String query){
        Member member = memberQueryService.findMember(memberDTO);

        // 검색 범위 지정 (제목, 본문내용, 메모)
        Specification<Scrap> inquiryRange = Specification.where(ScrapSpecification.containingTitle(query))
                .or(ScrapSpecification.containingDescription(query))
                .or(ScrapSpecification.containingMemo(query));

        // 쿼리 생성
        Specification<Scrap> spec = Specification.where(ScrapSpecification.equalMember(member))
                .and(ScrapSpecification.isFavorite())
                .and(inquiryRange);

        return scrapRepository.findAll(spec, sort);
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
     * 스크랩 찾기
     *
     * @throws BaseException 리스트 중 존재하지 않는 스크랩이 있을 경우
     */
    @Override
    public List<Scrap> findScrapList(List<Long> scrapIdList) {
        List<Scrap> scrapList = scrapRepository.findAllById(scrapIdList);

        if(scrapIdList.size() != scrapList.size()){
            throw new BaseException(ErrorCode.SCRAP_NOT_FOUND);
        }

        return scrapList;
    }
}
