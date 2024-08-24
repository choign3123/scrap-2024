package com.example.scrap.web.scrap;

import com.example.scrap.entity.Scrap;
import com.example.scrap.base.enums.QueryRange;
import com.example.scrap.entity.TrashScrap;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.scrap.dto.ScrapRequest;

import java.util.List;

public interface IScrapCommandService {

    /**
     * 스크랩 생성
     */
    public Scrap createScrap(MemberDTO memberDTO, Long categoryId, ScrapRequest.CreateScrapDTO request);


    /**
     * 스크랩 즐겨찾기(단건)
     */
    public Scrap toggleScrapFavorite(MemberDTO memberDTO, Long scrapId);

    /**
     * 스크랩 즐겨찾기(목록)
     */
    public List<Scrap> toggleScrapFavoriteList(MemberDTO memberDTO,
                                               boolean isAllFavorite, QueryRange queryRange, Long categoryId,
                                               ScrapRequest.ToggleScrapFavoriteListDTO request);

    /**
     * 스크랩 이동하기 (단건)
     */
    public Scrap moveCategoryOfScrap(MemberDTO memberDTO, Long scrapId, ScrapRequest.MoveCategoryOfScrapDTO request);

    /**
     * 스크랩 이동하기 (목록)
     */
    public List<Scrap> moveCategoryOfScraps(MemberDTO memberDTO, ScrapRequest.MoveCategoryOfScrapsDTO request,
                                            boolean isAllMove, QueryRange queryRange, Long categoryId);

    /**
     * 스크랩의 메모 수정
     */
    public Scrap updateScrapMemo(MemberDTO memberDTO, Long scrapId, ScrapRequest.UpdateScrapMemoDTO request);

    /**
     * 스크랩 휴지통에 버리기(단건)
     */
    public TrashScrap throwScrapIntoTrash(MemberDTO memberDTO, Long scrapId);

    /**
     * 스크랩 휴지통에 버리기(목록)
     */
    public List<TrashScrap> throwScrapListIntoTrash(MemberDTO memberDTO, boolean isAllDelete, QueryRange queryRange, Long categoryId, ScrapRequest.DeleteScrapListDTO request);

    /**
     * 스크랩 휴지통에 버리기
     */
    public TrashScrap throwScrapIntoTrash(Scrap scrap);

    /**
     * 스크랩 전체 삭제
     */
    public void deleteAllScrap(MemberDTO memberDTO);
}
