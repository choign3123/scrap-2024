package com.example.scrap.web.scrap;

import com.example.scrap.entity.Scrap;
import com.example.scrap.web.baseDTO.PressSelectionType;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.scrap.dto.ScrapRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface IScrapCommandService {

    /**
     * 스크랩 생성
     * @param memberDTO
     * @param categoryId
     * @param request
     * @return 생성된 스크랩
     */
    public Scrap createScrap(MemberDTO memberDTO, Long categoryId, ScrapRequest.CreateScrapDTO request);


    /**
     * 스크랩 즐겨찾기(단건)
     * @param memberDTO
     * @param scrapId
     * @return
     */
    public Scrap toggleScrapFavorite(MemberDTO memberDTO, Long scrapId);

    /**
     * 스크랩 즐겨찾기(목록)
     * @param memberDTO
     * @param isAllFavorite
     * @param pressSelectionType
     * @param categoryId
     * @param request
     */
    public List<Scrap> toggleScrapFavoriteList(MemberDTO memberDTO,
                                        boolean isAllFavorite, PressSelectionType pressSelectionType, Long categoryId,
                                        ScrapRequest.ToggleScrapFavoriteListDTO request);

    /**
     * 스크랩 이동하기 (단건)
     * @param memberDTO
     * @param scrapId
     * @param request
     * @return
     */
    public Scrap moveCategoryOfScrap(MemberDTO memberDTO, Long scrapId, ScrapRequest.MoveCategoryOfScrapDTO request);

    /**
     * 스크랩 이동하기 (목록)
     * @param memberDTO
     * @param request
     * @param isAllMove
     * @param pressSelectionType
     * @param categoryId
     * @return
     */
    public List<Scrap> moveCategoryOfScraps(MemberDTO memberDTO, ScrapRequest.MoveCategoryOfScrapsDTO request,
                                            boolean isAllMove, PressSelectionType pressSelectionType, Long categoryId);

    /**
     * 스크랩의 메모 수정
     * @param memberDTO
     * @param scrapId
     * @param request
     * @return
     */
    public Scrap updateScrapMemo(MemberDTO memberDTO, Long scrapId, ScrapRequest.UpdateScrapMemoDTO request);

    /**
     * 스크랩 삭제(단건)
     * @param memberDTO
     * @param scrapId
     */
    public void deleteScrap(MemberDTO memberDTO, Long scrapId);

    /**
     * 스크랩 삭제(목록)
     * @param memberDTO
     * @param isAllDelete
     * @param pressSelectionType
     * @param categoryId
     * @param request
     */
    public void deleteScrapList(MemberDTO memberDTO, boolean isAllDelete, PressSelectionType pressSelectionType, Long categoryId, ScrapRequest.DeleteScrapListDTO request);
}
