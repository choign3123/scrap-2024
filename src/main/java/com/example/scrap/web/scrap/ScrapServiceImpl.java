package com.example.scrap.web.scrap;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.base.exception.ValidationException;
import com.example.scrap.converter.ScrapConverter;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.specification.ScrapSpecification;
import com.example.scrap.web.baseDTO.PressSelectionType;
import com.example.scrap.web.category.ICategoryService;
import com.example.scrap.web.member.IMemberService;
import com.example.scrap.web.member.MemberDTO;
import com.example.scrap.web.scrap.dto.ScrapRequest;
import lombok.RequiredArgsConstructor;
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
public class ScrapServiceImpl implements IScrapService{

    private final IMemberService memberService;
    private final ICategoryService categoryService;
    private final ScrapRepository scrapRepository;

    /**
     * 스크랩 생성
     * @param memberDTO
     * @param categoryId
     * @param request
     * @return 생성된 스크랩
     */
    @Transactional
    public Scrap createScrap(MemberDTO memberDTO, Long categoryId, ScrapRequest.CreateScrap request){
        Member member = memberService.findMember(memberDTO);
        Category category = categoryService.findCategory(categoryId);

        if(category.isIllegalMember(member)){
            throw new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH);
        }

        Scrap newScrap = ScrapConverter.toEntity(request, member, category);

        scrapRepository.save(newScrap);

        return newScrap;
    }

    /**
     * 스크랩 전체 조회 - 카테고리별
     * @param memberDTO
     * @param categoryId
     * @param pageRequest
     * @return
     */
    public Page<Scrap> getScrapListByCategory(MemberDTO memberDTO, Long categoryId, PageRequest pageRequest){
        Member member = memberService.findMember(memberDTO);
        Category category = categoryService.findCategory(categoryId);

        if(category.isIllegalMember(member)){
            throw new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH);
        }

//        Page<Scrap> scrapPage = scrapRepository.findAllByMemberAndCategoryAndStatus(member, category, ScrapStatus.ACTIVE, pageRequest);
        Specification<Scrap> spec = Specification.where(ScrapSpecification.isAvailable())
                .and(ScrapSpecification.equalMember(member))
                .and(ScrapSpecification.equalCategory(category));

        return  scrapRepository.findAll(spec, pageRequest);
    }

    /**
     * 즐겨찾기된 스크랩 조회
     * @param memberDTO
     * @param pageRequest
     * @return 즐겨찾기된 스크랩
     */
    public Page<Scrap> getFavoriteScrapList(MemberDTO memberDTO, PageRequest pageRequest){
        Member member = memberService.findMember(memberDTO);

        Specification<Scrap> spec = Specification.where(ScrapSpecification.isAvailable())
                .and(ScrapSpecification.equalMember(member))
                .and(ScrapSpecification.isFavorite());

//        return scrapRepository.findAllByMemberAndFavoriteAndStatus(member, true, ScrapStatus.ACTIVE, pageRequest);
        return scrapRepository.findAll(spec, pageRequest);
    }

    /**
     * 스크랩 세부 조회
     * @param memberDTO
     * @param scrapId
     * @return
     */
    public Scrap getScrapDetails(MemberDTO memberDTO, Long scrapId){
        Member member = memberService.findMember(memberDTO);

        Scrap scrap = findScrap(scrapId);

        if(scrap.isIllegalMember(member)){
            throw new BaseException(ErrorCode.SCRAP_MEMBER_NOT_MATCH);
        }

        return scrap;
    }

    /**
     * 스크랩 제목으로 검색 - 카테고리별
     * @param memberDTO
     * @param categoryId
     * @param query
     * @param sort
     * @return
     */
    public List<Scrap> findScrapByTitle(MemberDTO memberDTO, Long categoryId, String query, Sort sort){
        Member member = memberService.findMember(memberDTO);
        Category category = categoryService.findCategory(categoryId);

        if(category.isIllegalMember(member)){
            throw new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH);
        }

        Specification<Scrap> spec = Specification.where(ScrapSpecification.isAvailable())
                .and(ScrapSpecification.equalMember(member))
                .and(ScrapSpecification.equalCategory(category))
                .and(ScrapSpecification.containingTitle(query));

//        return scrapRepository.findAllByMemberAndCategoryAndTitleContainingAndStatus(member, category, query, ScrapStatus.ACTIVE, sort);
        return scrapRepository.findAll(spec, sort);
    }

    /**
     * 스크랩 삭제(단건)
     * @param memberDTO
     * @param scrapId
     */
    @Transactional
    public void deleteScrap(MemberDTO memberDTO, Long scrapId){
        Member member = memberService.findMember(memberDTO);
        Scrap scrap = findScrap(scrapId);

        if(scrap.isIllegalMember(member)){
            throw new BaseException(ErrorCode.SCRAP_MEMBER_NOT_MATCH);
        }

        scrap.toTrash();
    }

    /**
     * 스크랩 삭제(목록)
     * @param memberDTO
     * @param isAllDelete
     * @param pressSelectionType
     * @param categoryId
     * @param request
     */
    @Transactional
    public void deleteScrapList(MemberDTO memberDTO, boolean isAllDelete, PressSelectionType pressSelectionType, Long categoryId, ScrapRequest.DeleteScrapList request){
        Member member = memberService.findMember(memberDTO);
        List<Scrap> deleteScrapList = new ArrayList<>();

        // 모든 스크랩 삭제
        if(isAllDelete){
            Specification<Scrap> spec = Specification.where(ScrapSpecification.isAvailable())
                    .and(ScrapSpecification.equalMember(member));

            // 어떤 프레스 타입인지
            switch (pressSelectionType){
                case CATEGORY -> {
                    Category category = categoryService.findCategory(categoryId);
                    spec = spec.and(ScrapSpecification.equalCategory(category));
                }
                case FAVORITE -> {
                    spec = spec.and(ScrapSpecification.isFavorite());
                }
            }

            deleteScrapList = scrapRepository.findAll(spec);
        }
        // 요청된 스크랩만 삭제
        else{
            // 빈 리스트인 경우
            if(request.getScrapIdList().size() == 0){
                throw new ValidationException("scraps", "적어도 하나 이상의 스크랩을 포함하여야 됩니다.");
            }

            for(Long scrapId : request.getScrapIdList()){
                Scrap deleteScrap = findScrap(scrapId);

                if(deleteScrap.isIllegalMember(member)){
                    throw new BaseException(ErrorCode.SCRAP_MEMBER_NOT_MATCH);
                }

                deleteScrapList.add(deleteScrap);
            }
        }

        for(Scrap scrap : deleteScrapList){
            scrap.toTrash();
        }
    }

    public Scrap findScrap(Long scrapId){
        return scrapRepository.findById(scrapId).get();
    }
}
