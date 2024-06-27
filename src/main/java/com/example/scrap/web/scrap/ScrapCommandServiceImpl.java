package com.example.scrap.web.scrap;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.converter.ScrapConverter;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.base.enums.QueryRange;
import com.example.scrap.web.category.ICategoryQueryService;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.scrap.dto.ScrapRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScrapCommandServiceImpl implements IScrapCommandService {

    private final IMemberQueryService memberService;
    private final ICategoryQueryService categoryService;
    private final ScrapRepository scrapRepository;
    private final IScrapQueryService scrapQueryService;

    /**
     * 스크랩 생성
     * @param memberDTO
     * @param categoryId
     * @param request
     * @return 생성된 스크랩
     */
    public Scrap createScrap(MemberDTO memberDTO, Long categoryId, ScrapRequest.CreateScrapDTO request){
        Member member = memberService.findMember(memberDTO);
        Category category = categoryService.findCategory(categoryId);

        category.checkIllegalMember(member);

        Scrap newScrap = ScrapConverter.toEntity(request, member, category);

        scrapRepository.save(newScrap);

        return newScrap;
    }

    /**
     * 스크랩 즐겨찾기(단건)
     * @param memberDTO
     * @param scrapId
     * @return
     */
    @Transactional
    public Scrap toggleScrapFavorite(MemberDTO memberDTO, Long scrapId){
        Member member = memberService.findMember(memberDTO);
        Scrap scrap = scrapQueryService.findScrap(scrapId);

        scrap.checkIllegalMember(member);

        scrap.toggleFavorite();

        return scrap;
    }

    /**
     * 스크랩 즐겨찾기(목록)
     * @param memberDTO
     * @param isAllFavorite
     * @param queryRange
     * @param categoryId
     * @param request
     */
    @Transactional
    public List<Scrap> toggleScrapFavoriteList(MemberDTO memberDTO,
                                               boolean isAllFavorite, QueryRange queryRange, Long categoryId,
                                               ScrapRequest.ToggleScrapFavoriteListDTO request){

        Member member = memberService.findMember(memberDTO);
        List<Scrap> favoriteScrapList;

        // 전체 즐겨찾기
        if(isAllFavorite){
            favoriteScrapList = scrapQueryService.findAllByQueryType(member, queryRange, categoryId);
        }
        // 요청된 스크랩만 즐겨찾기
        else{
            favoriteScrapList = scrapQueryService.findAllByRequest(request.getScrapIdList(), member);
        }

        /**
         * 즐겨찾기 해제인지, 즐겨찾기인지 알아내기
         * ★ ☆ ★ ☆ = 즐겨찾기 O
         * ☆ ☆ ☆ ☆ = 즐겨찾기 O
         * ★ ★ ★ ★ = 즐겨찾기 X
         */
        boolean toggle = false;
        for(Scrap scrap : favoriteScrapList){
            if(!scrap.getIsFavorite()){
                toggle = true; // 선택된 스크랩중 하나라도 즐겨찾기X인게 있으면, 해당 스크랩 목록 전체 즐겨찾기하기.
                break;
            }
        }

        for(Scrap scrap : favoriteScrapList){
            scrap.toggleFavorite(toggle);
        }

        return favoriteScrapList;
    }

    /**
     * 스크랩 이동하기 (단건)
     * @param memberDTO
     * @param scrapId
     * @param request
     * @return
     */
    @Transactional
    public Scrap moveCategoryOfScrap(MemberDTO memberDTO, Long scrapId, ScrapRequest.MoveCategoryOfScrapDTO request){
        Member member = memberService.findMember(memberDTO);
        Scrap scrap = scrapQueryService.findScrap(scrapId);
        Category moveCategory = categoryService.findCategory(request.getMoveCategoryId());

        // 해당 스크랩에 접근할 수 있는지 확인
        scrap.checkIllegalMember(member);
        if(moveCategory.isIllegalMember(member)){
            throw new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH_IN_SCRAP);
        }

        // 스크랩 이동하기
        scrap.moveCategory(moveCategory);

        return scrap;
    }

    /**
     * 스크랩 이동하기 (목록)
     * @param memberDTO
     * @param request
     * @param isAllMove
     * @param queryRange
     * @param categoryId
     * @return
     */
    @Transactional
    public List<Scrap> moveCategoryOfScraps(MemberDTO memberDTO, ScrapRequest.MoveCategoryOfScrapsDTO request,
                                            boolean isAllMove, QueryRange queryRange, Long categoryId){

        Member member = memberService.findMember(memberDTO);
        Category moveCategory = categoryService.findCategory(request.getMoveCategoryId());
        List<Scrap> moveScrapList;

        // 해당 스크랩에 접근할 수 있는지 확인
        if(moveCategory.isIllegalMember(member)){
            throw new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH_IN_SCRAP);
        }

        // 전체 이동하기
        if(isAllMove){
            moveScrapList = scrapQueryService.findAllByQueryType(member, queryRange, categoryId);
        }
        // 요청된 스크랩만 이동하기
        else{
            moveScrapList = scrapQueryService.findAllByRequest(request.getScrapIdList(), member);
        }

        for(Scrap scrap : moveScrapList){
            scrap.moveCategory(moveCategory);
        }

        return moveScrapList;
    }

    /**
     * 스크랩의 메모 수정
     * @param memberDTO
     * @param scrapId
     * @param request
     * @return
     */
    @Transactional
    public Scrap updateScrapMemo(MemberDTO memberDTO, Long scrapId, ScrapRequest.UpdateScrapMemoDTO request){
        Member member = memberService.findMember(memberDTO);
        Scrap scrap = scrapQueryService.findScrap(scrapId);

        scrap.checkIllegalMember(member);

        scrap.updateMemo(request.getMemo());

        return scrap;
    }

    /**
     * 스크랩 삭제(단건)
     * @param memberDTO
     * @param scrapId
     */
    @Transactional
    public void thrwoScrapInTrash(MemberDTO memberDTO, Long scrapId){
        Member member = memberService.findMember(memberDTO);
        Scrap scrap = scrapQueryService.findScrap(scrapId);

        scrap.checkIllegalMember(member);

        scrap.toTrash();
    }

    /**
     * 스크랩 삭제(목록)
     * @param memberDTO
     * @param isAllDelete
     * @param queryRange
     * @param categoryId
     * @param request
     */
    @Transactional
    public void throwScrapListInTrash(MemberDTO memberDTO, boolean isAllDelete, QueryRange queryRange, Long categoryId, ScrapRequest.DeleteScrapListDTO request){
        Member member = memberService.findMember(memberDTO);
        List<Scrap> deleteScrapList;

        // 모든 스크랩 삭제
        if(isAllDelete){
            deleteScrapList = scrapQueryService.findAllByQueryType(member, queryRange, categoryId);
        }
        // 요청된 스크랩만 삭제
        else{
            deleteScrapList = scrapQueryService.findAllByRequest(request.getScrapIdList(), member);
        }

        for(Scrap scrap : deleteScrapList){
            scrap.toTrash();
        }
    }
}
