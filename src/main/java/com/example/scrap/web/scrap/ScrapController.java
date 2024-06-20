package com.example.scrap.web.scrap;

import com.example.scrap.base.exception.ValidationException;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.ScrapConverter;
import com.example.scrap.entity.Scrap;
import com.example.scrap.jwt.TokenProvider;
import com.example.scrap.validation.annotaion.*;
import com.example.scrap.base.Data;
import com.example.scrap.web.baseDTO.PressSelectionType;
import com.example.scrap.web.baseDTO.Sorts;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.scrap.dto.ScrapRequest;
import com.example.scrap.web.scrap.dto.ScrapResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort.Direction;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/scraps")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ScrapController {

    private final IScrapQueryService scrapQueryService;
    private final IScrapCommandService scrapCommandService;
    private final TokenProvider tokenProvider;

    /**
     * [POST] /scraps/{category-id}
     * [API-24] 스크랩 생성
     */
    @PostMapping("/{category-id}")
    public ResponseEntity<ResponseDTO> scrapSave(@RequestHeader("Authorization") String token, @PathVariable("category-id") @ExistCategory Long categoryId,
                                                 @RequestBody @Validated ScrapRequest.CreateScrapDTO request){

        MemberDTO memberDTO = tokenProvider.parseMemberDTO(token);

        Scrap newScrap = scrapCommandService.createScrap(memberDTO, categoryId, request);
        ScrapResponse.CreateScrapDTO response = ScrapConverter.toCreateScrapDTO(newScrap);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [GET] /scraps?category=&sort=&direction=&page=&size
     * [API-11] 스크랩 전체 조회-카테고리별
     */
    @GetMapping()
    public ResponseEntity<ResponseDTO> scrapListByCategory(@RequestHeader("Authorization") String token,
                                           @RequestParam("category") @ExistCategory Long categoryId,
                                           @RequestParam(name = "sort", defaultValue = "SCRAP_DATE") @EnumValid(enumC = Sorts.class) String sort,
                                           @RequestParam(name = "direction", defaultValue = "ASC") @EnumValid(enumC = Direction.class) String direction,
                                           @RequestParam(name = "page", defaultValue = "1") @PagingPage int page,
                                           @RequestParam(name = "size", defaultValue = Data.PAGING_SIZE) @PagingSize int size){

        MemberDTO memberDTO = tokenProvider.parseMemberDTO(token);

        log.info("sort: {}, direction: {}, page: {}, size: {}", sort, direction, page, size);

        // string -> enum 변경
        Sorts sortsEnum = Sorts.valueOf(sort.toUpperCase());
        Direction directionEnum = Direction.valueOf(direction.toUpperCase());

        // 페이지네이션
        PageRequest pageRequest = PageRequest.of(page-1, size, directionEnum, sortsEnum.getName());

        Page<Scrap> scrapPage = scrapQueryService.getScrapListByCategory(memberDTO, categoryId, pageRequest);
        ScrapResponse.GetScrapListByCategoryDTO response = ScrapConverter.toGetScrapListByCategory(scrapPage);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [GET] /scraps/favorite
     * [API-21] 즐겨찾기된 스크랩 조회
     */
    @GetMapping("/favorite")
    public ResponseEntity<ResponseDTO> favoriteScrapList(@RequestHeader("Authorization") String token,
                                         @RequestParam(name = "sort", defaultValue = "SCRAP_DATE") @EnumValid(enumC = Sorts.class) String sort,
                                         @RequestParam(name = "direction", defaultValue = "ASC") @EnumValid(enumC = Direction.class) String direction,
                                         @RequestParam(name = "page", defaultValue = "1") @PagingPage int page,
                                         @RequestParam(name = "size", defaultValue = Data.PAGING_SIZE) @PagingSize int size){

        MemberDTO memberDTO = tokenProvider.parseMemberDTO(token);

        // string -> enum 변경
        Sorts sortsEnum = Sorts.valueOf(sort.toUpperCase());
        Direction directionEnum = Direction.valueOf(direction.toUpperCase());
        // 페이지네이션
        PageRequest pageRequest = PageRequest.of(page-1, size, directionEnum, sortsEnum.getName());

        Page<Scrap> scrapPage = scrapQueryService.getFavoriteScrapList(memberDTO, pageRequest);
        ScrapResponse.GetFavoriteScrapListDTO response = ScrapConverter.toGetFavoriteScrapList(scrapPage);

        return ResponseEntity.ok(new ResponseDTO(response));

    }

    /**
     * [GET] /scraps/{scrap-id}
     * [API-12] 스크랩 세부 조회
     */
    @GetMapping("/{scrap-id}")
    public ResponseEntity<ResponseDTO> scrapDetails(@RequestHeader("Authorization") String token, @PathVariable("scrap-id") @ExistAvailableScrap Long scrapId){
        MemberDTO memberDTO = tokenProvider.parseMemberDTO(token);

        Scrap scrap = scrapQueryService.getScrapDetails(memberDTO, scrapId);
        ScrapResponse.GetScrapDetailsDTO response = ScrapConverter.toGetScrapDetails(scrap);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [GET] /scraps/search/title
     * [API-20] 스크랩 제목으로 검색-카테고리별
     */
    @GetMapping("/search/title")
    public ResponseEntity<ResponseDTO> scrapSearchByTitle(@RequestHeader("Authorization") String token,
                                          @RequestParam("category") @ExistCategory Long categoryId,
                                          @RequestParam("q") @NotBlank String query,
                                          @RequestParam(name = "sort", defaultValue = "SCRAP_DATE") @EnumValid(enumC = Sorts.class) String sorts,
                                          @RequestParam(name = "direction", defaultValue = "ASC") @EnumValid(enumC = Direction.class) String direction){

        MemberDTO memberDTO = tokenProvider.parseMemberDTO(token);

        // string -> enum 변경
        Sorts sortsEnum = Sorts.valueOf(sorts.toUpperCase());
        Direction directionEnum = Direction.valueOf(direction.toUpperCase());
        // 정렬
        Sort sortWay = Sort.by(directionEnum, sortsEnum.getName());

        List<Scrap> scrapList = scrapQueryService.findScrapByTitle(memberDTO, categoryId, query, sortWay);
        ScrapResponse.FindScrapByTitleDTO response = ScrapConverter.toFindScrapByTitle(scrapList);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [GET] /scraps/share
     * [API-31] 스크랩 전체 공유하기
     */
    @GetMapping("/share")
    public ResponseEntity<ResponseDTO> allScrapShare(@RequestHeader("Authorization") String token,
                                     @RequestParam(name = "type", required = false) @EnumValid(enumC = PressSelectionType.class) String pressSelectionStr,
                                     @RequestParam(name = "category", required = false) @ExistCategory(required = false) Long categoryId){

        MemberDTO memberDTO = tokenProvider.parseMemberDTO(token);

        PressSelectionType pressSelectionType = PressSelectionType.valueOf(pressSelectionStr.toUpperCase());

        checkCategoryMissing(categoryId, pressSelectionType);

        List<Scrap> scrapList = scrapQueryService.shareAllScrap(memberDTO, pressSelectionType, categoryId);
        ScrapResponse.ShareAllScrapDTO response = ScrapConverter.toShareAllScrap(scrapList);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [PATCH] /scraps/{scrap-id}/favorite
     * [API-16] 스크랩 즐겨찾기 (단건)
     */
    @PatchMapping("{scrap-id}/favorite")
    public ResponseEntity<ResponseDTO> scrapFavoriteToggle(@RequestHeader("Authorization") String token, @PathVariable("scrap-id") @ExistAvailableScrap Long scrapId){

        MemberDTO memberDTO = tokenProvider.parseMemberDTO(token);

        Scrap scrap = scrapCommandService.toggleScrapFavorite(memberDTO, scrapId);
        ScrapResponse.ToggleScrapFavoriteDTO response = ScrapConverter.toToggleScrapFavorite(scrap);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [PATCH] /scraps/favorite
     * [API-17] 스크랩 즐겨찾기 (목록)
     */
    @PatchMapping("/favorite")
    public ResponseEntity<ResponseDTO> scrapFavoriteListToggle(@RequestHeader("Authorization") String token,
                                               @RequestParam(name = "all", defaultValue = "false", required = false) boolean isAllFavorite,
                                               @RequestParam(name = "type", required = false) @EnumValid(enumC = PressSelectionType.class, required = false) String pressSelectionStr,
                                               @RequestParam(name = "category", required = false) @ExistCategory(required = false) Long categoryId,
                                               @RequestBody @Validated ScrapRequest.ToggleScrapFavoriteListDTO request){

        MemberDTO memberDTO = tokenProvider.parseMemberDTO(token);

        PressSelectionType pressSelectionType = null;
        if(isAllFavorite){
            // 프레스 선택 타입 누락 확인
            pressSelectionType = checkPressSelectionTypeMissing(pressSelectionStr);

            // 카테고리 누락 확인
            checkCategoryMissing(categoryId, pressSelectionType);
        }

        List<Scrap> scrapList = scrapCommandService.toggleScrapFavoriteList(memberDTO, isAllFavorite, pressSelectionType, categoryId, request);
        ScrapResponse.ToggleScrapFavoriteListDTO response = ScrapConverter.toToggleScrapFavoriteList(scrapList);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [PATCH] /scraps/{scrap-id}/move
     * [API-15] 스크랩 이동하기 (단건)
     */
    @PatchMapping("/{scrap-id}/move")
    public ResponseEntity<ResponseDTO> categoryOfScrapMove(@RequestHeader("Authorization") String token, @PathVariable("scrap-id") @ExistAvailableScrap Long scrapId,
                                           @RequestBody @Validated ScrapRequest.MoveCategoryOfScrapDTO request){

        MemberDTO memberDTO = tokenProvider.parseMemberDTO(token);

        Scrap scrap = scrapCommandService.moveCategoryOfScrap(memberDTO, scrapId, request);
        ScrapResponse.MoveCategoryOfScrapDTO response = ScrapConverter.toMoveCategoryOfScrap(scrap);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [PATCH] /scraps/move
     * [API-19] 스크랩 이동하기 (목록)
     */
    @PatchMapping("/move")
    public ResponseEntity<ResponseDTO> categoryOfScrapsMove(@RequestHeader("Authorization") String token, @RequestBody @Validated ScrapRequest.MoveCategoryOfScrapsDTO request,
                                            @RequestParam(name = "all", defaultValue = "false", required = false) boolean isAllMove,
                                            @RequestParam(name = "type", required = false) @EnumValid(enumC = PressSelectionType.class, required = false) String pressSelectionStr,
                                            @RequestParam(name = "category", required = false) @ExistCategory(required = false) Long categoryId){

        MemberDTO memberDTO = tokenProvider.parseMemberDTO(token);

        PressSelectionType pressSelectionType = null;
        if(isAllMove){
            // 프레스 선택 타입 누락 확인
            pressSelectionType = checkPressSelectionTypeMissing(pressSelectionStr);

            // 카테고리 누락 확인
            checkCategoryMissing(categoryId, pressSelectionType);
        }

        List<Scrap> scrapList = scrapCommandService.moveCategoryOfScraps(memberDTO, request, isAllMove, pressSelectionType, categoryId);
        ScrapResponse.MoveCategoryOfScrapListDTO response = ScrapConverter.toMoveCategoryOfScraps(scrapList);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [PATCH] /scraps/{scrap-id}/memo
     * [API-14] 스크랩의 메모 수정
     */
    @PatchMapping("/{scrap-id}/memo")
    public ResponseEntity<ResponseDTO> scrapMemoModify(@RequestHeader("Authorization") String token, @PathVariable("scrap-id") @ExistAvailableScrap Long scrapId,
                                       @RequestBody @Validated ScrapRequest.UpdateScrapMemoDTO request){

        MemberDTO memberDTO = tokenProvider.parseMemberDTO(token);

        Scrap scrap = scrapCommandService.updateScrapMemo(memberDTO, scrapId, request);
        ScrapResponse.UpdateScrapMemoDTO response = ScrapConverter.toUpdateScrapMemo(scrap);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [PATCH] /scraps/{scrap-id}/trash
     * [API-13] 스크랩 삭제 (단건)
     */
    @PatchMapping("/{scrap-id}/trash")
    public ResponseEntity<ResponseDTO> scrapRemove(@RequestHeader("Authorization") String token, @PathVariable("scrap-id") @ExistAvailableScrap Long scrapId){

        MemberDTO memberDTO = tokenProvider.parseMemberDTO(token);

        scrapCommandService.deleteScrap(memberDTO, scrapId);

        return ResponseEntity.ok(new ResponseDTO<Void>());
    }

    /**
     * [PATCH] /scraps/trash
     * [API-18] 스크랩 삭제 (목록)
     */
    @PatchMapping("/trash")
    public ResponseEntity<ResponseDTO> scrapListRemove(@RequestHeader("Authorization") String token, @RequestBody @Validated ScrapRequest.DeleteScrapListDTO request,
                                       @RequestParam(name = "all", defaultValue = "false", required = false) boolean isAllDelete,
                                       @RequestParam(name = "type", required = false) @EnumValid(enumC = PressSelectionType.class, required = false) String pressSelectionStr,
                                       @RequestParam(name = "category", required = false) @ExistCategory(required = false) Long categoryId){

        MemberDTO memberDTO = tokenProvider.parseMemberDTO(token);

        // string -> enum
        PressSelectionType pressSelectionType = null;
        if(isAllDelete){
            // 프레스 선택 타입 누락
            pressSelectionType = checkPressSelectionTypeMissing(pressSelectionStr);

            // 카테고리 누락
            checkCategoryMissing(categoryId, pressSelectionType);
        }

        scrapCommandService.deleteScrapList(memberDTO, isAllDelete, pressSelectionType, categoryId, request);

        return ResponseEntity.ok(new ResponseDTO<Void>());
    }

    /**
     * 프레스 선택 타입 누락 확인
     */
    private PressSelectionType checkPressSelectionTypeMissing(String pressSelectionStr){
        boolean pressSelectionTypeMissing = (pressSelectionStr == null);
        if(pressSelectionTypeMissing){
            throw new ValidationException("type", "전체 선택일 시, 필수 입력입니다.");
        }

        return PressSelectionType.valueOf(pressSelectionStr.toUpperCase());
    }

    /**
     * 카테고리 누락 확인
     * @return if category missing throw ValidationException, else return true
     * @throws ValidationException
     */
    private boolean checkCategoryMissing(Long categoryId, PressSelectionType pressSelectionType){
        boolean categoryIdMissing = (pressSelectionType == PressSelectionType.CATEGORY) && (categoryId == null);
        if(categoryIdMissing){
            throw new ValidationException("category", "CATEGORY 타입일 시, 필수 입력입니다.");
        }

        return true;
    }
}
