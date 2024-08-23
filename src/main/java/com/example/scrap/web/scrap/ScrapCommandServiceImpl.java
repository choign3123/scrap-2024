package com.example.scrap.web.scrap;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.data.PolicyData;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.converter.ScrapConverter;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.base.enums.QueryRange;
import com.example.scrap.entity.TrashScrap;
import com.example.scrap.specification.ScrapSpecification;
import com.example.scrap.web.category.ICategoryQueryService;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.scrap.dto.ScrapRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScrapCommandServiceImpl implements IScrapCommandService {

    private final IMemberQueryService memberService; // TODO: 변수명 변경하기
    private final ICategoryQueryService categoryService;
    private final IScrapQueryService scrapQueryService;
    private final ScrapRepository scrapRepository;
    private final TrashScrapRepository trashScrapRepository;

    /**
     * 스크랩 생성하기
     * @throws BaseException 카테고리의 멤버와 요청멤버가 일치하지 않을 경우
     * @throws BaseException 스크랩 생성 개수를 초과할 경우
     */
    public Scrap createScrap(MemberDTO memberDTO, Long categoryId, ScrapRequest.CreateScrapDTO request){
        Member member = memberService.findMember(memberDTO);
        Category category = categoryService.findCategory(categoryId);

        category.checkIllegalMember(member);

        // 스크랩 생성 개수 제한 확인
        boolean isExceedScrapLimit = scrapRepository.countAllByMember(member) >= PolicyData.SCRAP_CREATE_LIMIT;
        if(isExceedScrapLimit){
            throw new BaseException(ErrorCode.EXCEED_SCRAP_CREATE_LIMIT);
        }

        Scrap newScrap = ScrapConverter.toEntity(request, member, category);

        scrapRepository.save(newScrap);

        return newScrap;
    }

    /**
     * 스크랩 즐겨찾기(단건)
     * @throws BaseException 스크랩의 멤버와 요청멤버가 일치하지 않을 경우
     */
    public Scrap toggleScrapFavorite(MemberDTO memberDTO, Long scrapId){
        Member member = memberService.findMember(memberDTO);
        Scrap scrap = scrapQueryService.findScrap(scrapId);

        scrap.checkIllegalMember(member);

        scrap.toggleFavorite();

        return scrap;
    }

    /**
     * 스크랩 즐겨찾기(목록)
     *
     * 즐겨찾기 해제인지, 즐겨찾기인지 알아내기
     * ★ ☆ ★ ☆ = 즐겨찾기 O
     * ☆ ☆ ☆ ☆ = 즐겨찾기 O
     * ★ ★ ★ ★ = 즐겨찾기 X
     *
     * @throws BaseException 카테고리의 멤버와 요청멤버가 일치하지 않을 경우
     */
    public List<Scrap> toggleScrapFavoriteList(MemberDTO memberDTO,
                                               boolean isAllFavorite, QueryRange queryRange, Long categoryId,
                                               ScrapRequest.ToggleScrapFavoriteListDTO request){

        Member member = memberService.findMember(memberDTO);

        // 동적인 쿼리 생성
        Specification<Scrap> spec = Specification.where(ScrapSpecification.equalMember(member));
        if(isAllFavorite){ // 전체 즐겨찾기 하기
            Category category = null;

            if(queryRange.equals(QueryRange.CATEGORY)){
                category = categoryService.findCategory(categoryId);
                category.checkIllegalMember(member);
            }

            spec = addQueryRangeSpec(spec, queryRange, category);
        }
        else{ // 요청된 스크랩에 대해서만 즐겨찾기 하기
            spec = spec.and(ScrapSpecification.inScrap(request.getScrapIdList()));
        }


        List<Scrap> scrapList = scrapRepository.findAll(spec);

        // 즐겨찾기 O/X 여부 판별하기
        boolean toggle = false;
        for(Scrap scrap : scrapList){
            if(!scrap.getIsFavorite()){
                toggle = true; // 선택된 스크랩중 하나라도 즐겨찾기X인게 있으면, 해당 스크랩 목록 전체 즐겨찾기하기.
                break;
            }
        }

        // 스크랩 즐겨찾기 설정하기
        for(Scrap scrap : scrapList){
            scrap.updateFavorite(toggle);
        }

        return scrapList;
    }

    /**
     * 스크랩 이동하기 (단건)
     * @throws BaseException 카테고리의 멤버와 요청멤버가 일치하지 않을 경우
     * @throws BaseException 스크랩의 멤버와 요청멤버가 일치하지 않을 경우
     */
    public Scrap moveCategoryOfScrap(MemberDTO memberDTO, Long scrapId, ScrapRequest.MoveCategoryOfScrapDTO request){
        Member member = memberService.findMember(memberDTO);
        Scrap scrap = scrapQueryService.findScrap(scrapId);
        Category moveCategory = categoryService.findCategory(request.getMoveCategoryId());

        // 해당 스크랩에 접근할 수 있는지 확인
        scrap.checkIllegalMember(member);
        // TODO: category.checkIllegalMember();로 변경하기
        if(moveCategory.isIllegalMember(member)){
            throw new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH_IN_SCRAP); // TODO: CATEGORY_MEMBER_NOT_MATCH 으로 변경하기
        }

        // 스크랩 이동하기
        scrap.moveCategory(moveCategory);

        return scrap;
    }

    /**
     * 스크랩 이동하기 (목록)
     * @throws BaseException 카테고리의 멤버와 요청멤버가 일치하지 않을 경우
     */
    public List<Scrap> moveCategoryOfScraps(MemberDTO memberDTO, ScrapRequest.MoveCategoryOfScrapsDTO request,
                                            boolean isAllMove, QueryRange queryRange, Long categoryId){

        Member member = memberService.findMember(memberDTO);
        Category moveCategory = categoryService.findCategory(request.getMoveCategoryId());

        // TODO: moveCategory member 체크하기

        // 동적인 쿼리 생성
        Specification<Scrap> spec = Specification.where(ScrapSpecification.equalMember(member));
        if(isAllMove){ // 전체 스크랩 이동하기
            Category category = null;

            if(queryRange.equals(QueryRange.CATEGORY)){
                category = categoryService.findCategory(categoryId);
                category.checkIllegalMember(member);
            }
            spec = addQueryRangeSpec(spec, queryRange, category);
        }
        else{ // 요청된 스크랩에 대해서만 이동하기
            spec = spec.and(ScrapSpecification.inScrap(request.getScrapIdList()));
        }

        List<Scrap> scrapList = scrapRepository.findAll(spec);
        for(Scrap scrap : scrapList){
            scrap.moveCategory(moveCategory);
        }

        return scrapList;
    }

    /**
     * 스크랩의 메모 수정
     * @throws BaseException 스크랩의 멤버와 요청멤버가 일치하지 않을 경우
     */
    public Scrap updateScrapMemo(MemberDTO memberDTO, Long scrapId, ScrapRequest.UpdateScrapMemoDTO request){
        Member member = memberService.findMember(memberDTO);
        Scrap scrap = scrapQueryService.findScrap(scrapId);

        scrap.checkIllegalMember(member);

        scrap.updateMemo(request.getMemo());

        return scrap;
    }

    /**
     * 스크랩 휴지통에 버리기(단건)
     * @throws BaseException 스크랩의 멤버와 요청멤버가 일치하지 않을 경우
     */
    public TrashScrap throwScrapIntoTrash(MemberDTO memberDTO, Long scrapId){
        Member member = memberService.findMember(memberDTO);
        Scrap scrap = scrapQueryService.findScrap(scrapId);

        scrap.checkIllegalMember(member);

        return throwScrapIntoTrash(scrap);
    }

    /**
     * 스크랩 휴지통에 버리기(목록)
     * @throws BaseException 카테고리의 멤버와 요청멤버가 일치하지 않을 경우
     */
    public List<TrashScrap> throwScrapListIntoTrash(MemberDTO memberDTO, boolean isAllDelete, QueryRange queryRange, Long categoryId, ScrapRequest.DeleteScrapListDTO request){
        Member member = memberService.findMember(memberDTO);

        // 동적인 쿼리 생성
        Specification<Scrap> spec = Specification.where(ScrapSpecification.equalMember(member));
        if(isAllDelete){ // 전체 휴지통에 버리기
            Category category = null;

            if(queryRange.equals(QueryRange.CATEGORY)){
                category = categoryService.findCategory(categoryId);
                category.checkIllegalMember(member);
            }

            spec = addQueryRangeSpec(spec, queryRange, category);
        }
        else{ // 요청된 스크랩에 대해서만 휴지통에 버리기
            spec = spec.and(ScrapSpecification.inScrap(request.getScrapIdList()));
        }

        List<Scrap> scrapList = scrapRepository.findAll(spec);

        // 스크랩 휴지통에 버리기
        List<TrashScrap> trashScrapList = new ArrayList<>();
        for(Scrap scrap : scrapList){
            trashScrapList.add(throwScrapIntoTrash(scrap));
        }

        return trashScrapList;
    }

    /**
     * 휴지통에 스크랩 버리기
     */
    public TrashScrap throwScrapIntoTrash(Scrap scrap){
        TrashScrap trashScrap = scrap.toTrash();

        scrapRepository.delete(scrap); // 스크랩 삭제
        trashScrapRepository.save(trashScrap); // 휴지통에 스크랩 보관

        return trashScrap;
    }

    /**
     * 스크랩 전체 삭제
     */
    public void deleteAllScrap(MemberDTO memberDTO){
        Member member = memberService.findMember(memberDTO);

        scrapRepository.deleteAllByMember(member);
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
