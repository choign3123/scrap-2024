package com.example.scrap.web.scrap;

import com.example.scrap.base.exception.ValidationException;
import com.example.scrap.base.response.ApiResponse;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.ScrapConverter;
import com.example.scrap.entity.Scrap;
import com.example.scrap.validation.annotaion.*;
import com.example.scrap.validation.validator.EnumValidValidator;
import com.example.scrap.validation.validator.ExistCategoriesValidator;
import com.example.scrap.web.baseDTO.Data;
import com.example.scrap.web.baseDTO.PressSelectionType;
import com.example.scrap.web.baseDTO.Sorts;
import com.example.scrap.web.member.MemberDTO;
import com.example.scrap.web.scrap.dto.ScrapRequest;
import com.example.scrap.web.scrap.dto.ScrapResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort.Direction;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/scraps")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ScrapController {

    private final IScrapService scrapService;

    /**
     * [POST] /scraps/{category-id}
     * [API-24] 스크랩 생성
     * @param memberId
     * @param categoryId
     * @param request
     * @return
     */
    @PostMapping("/{category-id}")
    public ApiResponse scrapSave(@RequestHeader("member-id") Long memberId, @PathVariable("category-id") @ExistCategory Long categoryId,
                                 @RequestBody @Validated ScrapRequest.CreateScrap request){

        MemberDTO memberDTO = new MemberDTO(memberId);

        Scrap newScrap = scrapService.createScrap(memberDTO, categoryId, request);

        ScrapResponse.CreateScrapDTO response = ScrapConverter.toCreateScrapDTO(newScrap);

        return new ApiResponse(new ResponseDTO<ScrapResponse.CreateScrapDTO>(response));
    }

    /**
     * [GET] /scraps?category=&sort=&direction=&page=&size
     * [API-11] 스크랩 전체 조회-카테고리별
     * @param memberId
     * @param categoryId
     * @param sort
     * @param direction
     * @param page
     * @param size
     * @return
     */
    @GetMapping()
    public ApiResponse scrapListByCategory(@RequestHeader("member-id") Long memberId,
                                           @RequestParam("category") @ExistCategory Long categoryId,
                                           @RequestParam(name = "sort", defaultValue = "SCRAP_DATE") @EnumValid(enumC = Sorts.class) String sort,
                                           @RequestParam(name = "direction", defaultValue = "ASC") @EnumValid(enumC = Direction.class) String direction,
                                           @RequestParam(name = "page", defaultValue = "1") @PagingPage int page,
                                           @RequestParam(name = "size", defaultValue = Data.PAGING_SIZE) @PagingSize int size){

        MemberDTO memberDTO = new MemberDTO(memberId);

        log.info("sort: {}, direction: {}, page: {}, size: {}", sort, direction, page, size);

        // string -> enum 변경
        Sorts sortsEnum = Sorts.valueOf(sort.toUpperCase());
        Direction directionEnum = Direction.valueOf(direction.toUpperCase());

        // 페이지네이션
        PageRequest pageRequest = PageRequest.of(page-1, size, directionEnum, sortsEnum.getName());

        Page<Scrap> scrapPage = scrapService.getScrapListByCategory(memberDTO, categoryId, pageRequest);

        ScrapResponse.GetScrapListByCategory response = ScrapConverter.toGetScrapListByCategory(scrapPage);

        return new ApiResponse(new ResponseDTO(response));
    }

    /**
     * [GET] /scraps/favorite
     * [API-21] 즐겨찾기된 스크랩 조회
     * @param memberId
     * @param sort
     * @param direction
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/favorite")
    public ApiResponse favoriteScrapList(@RequestHeader("member-id") Long memberId,
                                         @RequestParam(name = "sort", defaultValue = "SCRAP_DATE") @EnumValid(enumC = Sorts.class) String sort,
                                         @RequestParam(name = "direction", defaultValue = "ASC") @EnumValid(enumC = Direction.class) String direction,
                                         @RequestParam(name = "page", defaultValue = "1") @PagingPage int page,
                                         @RequestParam(name = "size", defaultValue = Data.PAGING_SIZE) @PagingSize int size){

        MemberDTO memberDTO = new MemberDTO(memberId);

        // string -> enum 변경
        Sorts sortsEnum = Sorts.valueOf(sort.toUpperCase());
        Direction directionEnum = Direction.valueOf(direction.toUpperCase());
        // 페이지네이션
        PageRequest pageRequest = PageRequest.of(page-1, size, directionEnum, sortsEnum.getName());

        Page<Scrap> scrapPage = scrapService.getFavoriteScrapList(memberDTO, pageRequest);
        ScrapResponse.GetFavoriteScrapList response = ScrapConverter.toGetFavoriteScrapList(scrapPage);

        return new ApiResponse(new ResponseDTO(response));

    }

    /**
     * [GET] /scraps/{scrap-id}
     * [API-12] 스크랩 세부 조회
     * @param memberId
     * @param scrapId
     * @return
     */
    @GetMapping("/{scrap-id}")
    public ApiResponse scrapDetails(@RequestHeader("member-id") Long memberId, @PathVariable("scrap-id") @ExistAvailableScrap Long scrapId){
        MemberDTO memberDTO = new MemberDTO(memberId);

        Scrap scrap = scrapService.getScrapDetails(memberDTO, scrapId);

        ScrapResponse.GetScrapDetails response = ScrapConverter.toGetScrapDetails(scrap);

        return new ApiResponse(new ResponseDTO<>(response));
    }

    /**
     * [GET] /scraps/search/title
     * [API-20] 스크랩 제목으로 검색-카테고리별
     * @param memberId
     * @param categoryId
     * @param query
     * @param sorts
     * @param direction
     * @return
     */
    @GetMapping("/search/title")
    public ApiResponse scrapSearchByTitle(@RequestHeader("member-id") Long memberId,
                                          @RequestParam("category") @ExistCategory Long categoryId,
                                          @RequestParam("q") @NotBlank String query,
                                          @RequestParam(name = "sort", defaultValue = "SCRAP_DATE") @EnumValid(enumC = Sorts.class) String sorts,
                                          @RequestParam(name = "direction", defaultValue = "ASC") @EnumValid(enumC = Direction.class) String direction){

        MemberDTO memberDTO = new MemberDTO(memberId);

        // string -> enum 변경
        Sorts sortsEnum = Sorts.valueOf(sorts.toUpperCase());
        Direction directionEnum = Direction.valueOf(direction.toUpperCase());
        // 정렬
        Sort sortWay = Sort.by(directionEnum, sortsEnum.getName());

        List<Scrap> scrapList = scrapService.findScrapByTitle(memberDTO, categoryId, query, sortWay);
        ScrapResponse.FindScrapByTitle response = ScrapConverter.toFindScrapByTitle(scrapList);

        return new ApiResponse(new ResponseDTO(response));
    }

    /**
     * [PATCH] /scraps/{scrap-id}/favorite
     * [API-16] 스크랩 즐겨찾기 (단건)
     * @param memberId
     * @param scrapId
     * @return
     */
    @PatchMapping("{scrap-id}/favorite")
    public ApiResponse scrapFavoriteToggle(@RequestHeader("member-id") Long memberId, @PathVariable("scrap-id") @ExistAvailableScrap Long scrapId){

        MemberDTO memberDTO = new MemberDTO(memberId);

        Scrap scrap = scrapService.toggleScrapFavorite(memberDTO, scrapId);
        ScrapResponse.ToggleScrapFavorite response = ScrapConverter.toToggleScrapFavorite(scrap);

        return new ApiResponse(new ResponseDTO(response));
    }

    /**
     * [PATCH] /scraps/favorite
     * [API-17] 스크랩 즐겨찾기 (목록)
     * @param memberId
     * @param isAllFavorite
     * @param pressSelectionType
     * @param categoryId
     * @param request
     * @return
     */
    @PatchMapping("/favorite")
    public ApiResponse scrapFavoriteListToggle(@RequestHeader("member-id") Long memberId,
                                               @RequestParam(name = "all", defaultValue = "false", required = false) boolean isAllFavorite,
                                               @RequestParam(name = "type", required = false) @EnumValid(enumC = PressSelectionType.class, required = false) String pressSelectionType,
                                               @RequestParam(name = "category", required = false) @ExistCategory(required = false) Long categoryId,
                                               @RequestBody @Validated ScrapRequest.ToggleScrapFavoriteList request){

        MemberDTO memberDTO = new MemberDTO(memberId);

        PressSelectionType pressSelectionTypeEnum = null;
        if(isAllFavorite){
            // 프레스 선택 타입 누락 확인
            boolean pressSelectionTypeMissing = (pressSelectionType == null);
            if(pressSelectionTypeMissing){
                throw new ValidationException("type", "모두 즐겨찾기일 시, 필수 입력입니다.");
            }
            pressSelectionTypeEnum = PressSelectionType.valueOf(pressSelectionType.toUpperCase());

            // 카테고리 누락 확인
            boolean categoryIdMissing = (pressSelectionTypeEnum == PressSelectionType.CATEGORY) && (categoryId == null);
            if(categoryIdMissing){
                throw new ValidationException("category", "CATEGORY 타입일 시, 필수 입력입니다.");
            }
        }

        List<Scrap> scrapList = scrapService.toggleScrapFavoriteList(memberDTO, isAllFavorite, pressSelectionTypeEnum, categoryId, request);
        ScrapResponse.ToggleScrapFavoriteList response = ScrapConverter.toToggleScrapFavoriteList(scrapList);

        return new ApiResponse(new ResponseDTO(response));
    }

    /**
     * [PATCH] /scraps/{scrap-id}/memo
     * [API-14] 스크랩의 메모 수정
     * @param memberId
     * @param scrapId
     * @param request
     * @return
     */
    @PatchMapping("/{scrap-id}/memo")
    public ApiResponse scrapMemoModify(@RequestHeader("member-id") Long memberId, @PathVariable("scrap-id") @ExistAvailableScrap Long scrapId,
                                       @RequestBody @Validated ScrapRequest.UpdateScrapMemo request){

        MemberDTO memberDTO = new MemberDTO(memberId);

        Scrap scrap = scrapService.updateScrapMemo(memberDTO, scrapId, request);
        ScrapResponse.UpdateScrapMemo response = ScrapConverter.toUpdateScrapMemo(scrap);

        return new ApiResponse(new ResponseDTO(response));
    }

    /**
     * [PATCH] /scraps/{scrap-id}/trash
     * [API-13] 스크랩 삭제 (단건)
     * @param memberId
     * @param scrapId
     * @return
     */
    @PatchMapping("/{scrap-id}/trash")
    public ApiResponse scrapRemove(@RequestHeader("member-id") Long memberId, @PathVariable("scrap-id") @ExistAvailableScrap Long scrapId){

        MemberDTO memberDTO = new MemberDTO(memberId);

        scrapService.deleteScrap(memberDTO, scrapId);

        return new ApiResponse(new ResponseDTO<Void>());
    }

    /**
     * [PATCH] /scraps/trash
     * [API-18] 스크랩 삭제 (목록)
     * @param memberId
     * @param request
     * @param isAllDelete
     * @param pressSelectionType
     * @param categoryId
     * @return
     */
    @PatchMapping("/trash")
    public ApiResponse scrapListRemove(@RequestHeader("member-id") Long memberId, @RequestBody @Validated ScrapRequest.DeleteScrapList request,
                                       @RequestParam(name = "all", defaultValue = "false", required = false) boolean isAllDelete,
                                       @RequestParam(name = "type", required = false) @EnumValid(enumC = PressSelectionType.class, required = false) String pressSelectionType,
                                       @RequestParam(name = "category", required = false) @ExistCategory(required = false) Long categoryId){

        MemberDTO memberDTO = new MemberDTO(memberId);

        // string -> enum
        PressSelectionType pressSelectionTypeEnum = null;
        if(isAllDelete){
            // 프레스 선택 타입 누락
            if(pressSelectionType == null){
                throw new ValidationException("type", "모두 삭제일 시, 필수 입력입니다.");
            }
            pressSelectionTypeEnum = PressSelectionType.valueOf(pressSelectionType.toUpperCase());

            // 카테고리 누락
            boolean categoryIdNeed = (pressSelectionTypeEnum == PressSelectionType.CATEGORY);
            boolean categoryIdMissing = categoryIdNeed && categoryId == null;
            if(categoryIdMissing){
                throw new ValidationException("category", "CATEGORY 타입일 시, 필수 입력입니다.");
            }
        }

        scrapService.deleteScrapList(memberDTO, isAllDelete, pressSelectionTypeEnum, categoryId, request);

        return new ApiResponse(new ResponseDTO<Void>());
    }

}
