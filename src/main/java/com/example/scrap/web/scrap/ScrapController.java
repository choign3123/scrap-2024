package com.example.scrap.web.scrap;

import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.ScrapConverter;
import com.example.scrap.entity.Scrap;
import com.example.scrap.jwt.ITokenProvider;
import com.example.scrap.validation.annotaion.*;
import com.example.scrap.base.data.DefaultData;
import com.example.scrap.base.enums.Sorts;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.scrap.dto.ScrapRequest;
import com.example.scrap.web.scrap.dto.ScrapResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "스크랩", description = "스크랩 관련 API")
public class ScrapController {

    private final IScrapQueryService scrapQueryService;
    private final IScrapCommandService scrapCommandService;
    private final ITokenProvider tokenProvider;

    /**
     * [POST] /scraps/{category-id}
     * [API-24] 스크랩 생성
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping("/{category-id}")
    public ResponseEntity<ResponseDTO<ScrapResponse.CreateScrapDTO>>
    scrapSave(@RequestHeader("Authorization") String token,
              @PathVariable("category-id") Long categoryId,
              @RequestBody @Validated ScrapRequest.CreateScrapDTO request){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        Scrap newScrap = scrapCommandService.createScrap(memberDTO, categoryId, request);
        ScrapResponse.CreateScrapDTO response = ScrapConverter.toCreateScrapDTO(newScrap);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * [GET] /scraps?category=&sort=&direction=&page=&size
     * [API-11] 스크랩 전체 조회-카테고리별
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping()
    public ResponseEntity<ResponseDTO<ScrapResponse.GetScrapListByCategoryDTO>>
    scrapListByCategory(@RequestHeader("Authorization") String token,
                        @RequestParam("category") Long categoryId,
                        @RequestParam(name = "sort", defaultValue = "SCRAP_DATE") @EnumValid(enumC = Sorts.class) String sort,
                        @RequestParam(name = "direction", defaultValue = "ASC") @EnumValid(enumC = Direction.class) String direction,
                        @RequestParam(name = "page", defaultValue = "0") @PagingPage int page,
                        @RequestParam(name = "size", defaultValue = DefaultData.PAGING_SIZE) @PagingSize int size){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        log.info("sort: {}, direction: {}, page: {}, size: {}", sort, direction, page, size);

        // string -> enum 변경
        Sorts sortsEnum = Sorts.valueOf(sort.toUpperCase());
        Direction directionEnum = Direction.valueOf(direction.toUpperCase());

        // 페이지네이션
        PageRequest pageRequest = PageRequest.of(page, size, directionEnum, sortsEnum.getName());

        Page<Scrap> scrapPage = scrapQueryService.getScrapListByCategory(memberDTO, categoryId, pageRequest);
        ScrapResponse.GetScrapListByCategoryDTO response = ScrapConverter.toGetScrapListByCategory(scrapPage);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * [GET] /scraps/favorite
     * [API-21] 즐겨찾기된 스크랩 조회
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping("/favorite")
    public ResponseEntity<ResponseDTO<ScrapResponse.GetFavoriteScrapListDTO>>
    favoriteScrapList(@RequestHeader("Authorization") String token,
                      @RequestParam(name = "sort", defaultValue = "SCRAP_DATE") @EnumValid(enumC = Sorts.class) String sort,
                      @RequestParam(name = "direction", defaultValue = "ASC") @EnumValid(enumC = Direction.class) String direction,
                      @RequestParam(name = "page", defaultValue = "0") @PagingPage int page,
                      @RequestParam(name = "size", defaultValue = DefaultData.PAGING_SIZE) @PagingSize int size){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        // string -> enum 변경
        Sorts sortsEnum = Sorts.valueOf(sort.toUpperCase());
        Direction directionEnum = Direction.valueOf(direction.toUpperCase());
        // 페이지네이션
        PageRequest pageRequest = PageRequest.of(page, size, directionEnum, sortsEnum.getName());

        Page<Scrap> scrapPage = scrapQueryService.getFavoriteScrapList(memberDTO, pageRequest);
        ScrapResponse.GetFavoriteScrapListDTO response = ScrapConverter.toGetFavoriteScrapList(scrapPage);

        return ResponseEntity.ok(new ResponseDTO<>(response));

    }

    /**
     * [GET] /scraps/{scrap-id}
     * [API-12] 스크랩 세부 조회
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping("/{scrap-id}")
    public ResponseEntity<ResponseDTO<ScrapResponse.GetScrapDetailsDTO>>
    scrapDetails(@RequestHeader("Authorization") String token,
                 @PathVariable("scrap-id") Long scrapId){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        Scrap scrap = scrapQueryService.getScrapDetails(memberDTO, scrapId);
        ScrapResponse.GetScrapDetailsDTO response = ScrapConverter.toGetScrapDetails(scrap);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * [GET] /scraps/search/{category-id}
     * [API-20] 스크랩 검색 (특정 카테고리에서)
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping("/search/{category-id}")
    public ResponseEntity<ResponseDTO<ScrapResponse.FindScrapAtParticularCategoryDTO>>
    scrapSearchAtParticularCategory(@RequestHeader("Authorization") String token,
                                    @PathVariable(name = "category-id") Long categoryId,
                                    @RequestParam(name = "sort", defaultValue = "SCRAP_DATE") @EnumValid(enumC = Sorts.class) String sorts,
                                    @RequestParam(name = "direction", defaultValue = "ASC") @EnumValid(enumC = Direction.class) String direction,
                                    @RequestParam("q") @NotBlank String query){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        // string -> enum 변경
        Sorts sortsEnum = Sorts.valueOf(sorts.toUpperCase());
        Direction directionEnum = Direction.valueOf(direction.toUpperCase());

        // 정렬
        Sort sort = Sort.by(directionEnum, sortsEnum.getName());

        List<Scrap> scrapList = scrapQueryService.findScrapAtParticularCategory(memberDTO, categoryId, sort, query);
        ScrapResponse.FindScrapAtParticularCategoryDTO response = ScrapConverter.toFindScrapAtParticularCategory(scrapList);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * [GET] /scraps/search/favorite
     * [API-36] 스크랩 검색 (즐겨찾기됨에서)
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping("/search/favorite")
    public ResponseEntity<ResponseDTO<ScrapResponse.FindScrapAtFavoriteDTO>>
    scrapSearchAtFavorite(@RequestHeader("Authorization") String token,
                          @RequestParam(name = "sort", defaultValue = "SCRAP_DATE") @EnumValid(enumC = Sorts.class) String sorts,
                          @RequestParam(name = "direction", defaultValue = "ASC") @EnumValid(enumC = Direction.class) String direction,
                          @RequestParam("q") @NotBlank String query){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        // string -> enum 변경
        Sorts sortsEnum = Sorts.valueOf(sorts.toUpperCase());
        Direction directionEnum = Direction.valueOf(direction.toUpperCase());

        // 정렬
        Sort sort = Sort.by(directionEnum, sortsEnum.getName());

        List<Scrap> scrapList = scrapQueryService.findScrapAtFavorite(memberDTO, sort, query);
        ScrapResponse.FindScrapAtFavoriteDTO response = ScrapConverter.toFindScrapAtFavorite(scrapList);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * [PATCH] /scraps/{scrap-id}/favorite
     * [API-16] 스크랩 즐겨찾기 (단건)
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PatchMapping("{scrap-id}/favorite")
    public ResponseEntity<ResponseDTO<ScrapResponse.ToggleScrapFavoriteDTO>>
    scrapFavoriteToggle(@RequestHeader("Authorization") String token,
                        @PathVariable("scrap-id") Long scrapId){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        Scrap scrap = scrapCommandService.toggleScrapFavorite(memberDTO, scrapId);
        ScrapResponse.ToggleScrapFavoriteDTO response = ScrapConverter.toToggleScrapFavorite(scrap);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * [PATCH] /scraps/favorite
     * [API-17] 스크랩 즐겨찾기 (목록)
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PatchMapping("/favorite")
    public ResponseEntity<ResponseDTO<ScrapResponse.ToggleScrapFavoriteListDTO>>
    scrapFavoriteListToggle(@RequestHeader("Authorization") String token,
                            @RequestBody @Validated ScrapRequest.ToggleScrapFavoriteListDTO request){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        List<Scrap> scrapList = scrapCommandService.toggleScrapFavoriteList(memberDTO, request);
        ScrapResponse.ToggleScrapFavoriteListDTO response = ScrapConverter.toToggleScrapFavoriteList(scrapList);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * [PATCH] /scraps/{scrap-id}/move
     * [API-15] 스크랩 이동하기 (단건)
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PatchMapping("/{scrap-id}/move")
    public ResponseEntity<ResponseDTO<ScrapResponse.MoveCategoryOfScrapDTO>>
    categoryOfScrapMove(@RequestHeader("Authorization") String token,
                        @PathVariable("scrap-id") Long scrapId,
                        @RequestBody @Validated ScrapRequest.MoveCategoryOfScrapDTO request){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        Scrap scrap = scrapCommandService.moveCategoryOfScrap(memberDTO, scrapId, request);
        ScrapResponse.MoveCategoryOfScrapDTO response = ScrapConverter.toMoveCategoryOfScrap(scrap);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * [PATCH] /scraps/move
     * [API-19] 스크랩 이동하기 (목록)
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PatchMapping("/move")
    public ResponseEntity<ResponseDTO<ScrapResponse.MoveCategoryOfScrapListDTO>>
    categoryOfScrapListMove(@RequestHeader("Authorization") String token,
                            @RequestBody @Validated ScrapRequest.MoveCategoryOfScrapsDTO request){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        List<Scrap> scrapList = scrapCommandService.moveCategoryOfScrapList(memberDTO, request);
        ScrapResponse.MoveCategoryOfScrapListDTO response = ScrapConverter.toMoveCategoryOfScrapList(scrapList);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * [PATCH] /scraps/{scrap-id}/memo
     * [API-14] 스크랩의 메모 수정
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PatchMapping("/{scrap-id}/memo")
    public ResponseEntity<ResponseDTO<ScrapResponse.UpdateScrapMemoDTO>>
    scrapMemoModify(@RequestHeader("Authorization") String token,
                    @PathVariable("scrap-id") Long scrapId,
                    @RequestBody @Validated ScrapRequest.UpdateScrapMemoDTO request){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        Scrap scrap = scrapCommandService.updateScrapMemo(memberDTO, scrapId, request);
        ScrapResponse.UpdateScrapMemoDTO response = ScrapConverter.toUpdateScrapMemo(scrap);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * [PATCH] /scraps/{scrap-id}/trash
     * [API-13] 스크랩 삭제 (단건)
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PatchMapping("/{scrap-id}/trash")
    public ResponseEntity<ResponseDTO<Void>> scrapRemove(@RequestHeader("Authorization") String token,
                                                   @PathVariable("scrap-id") Long scrapId){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        scrapCommandService.throwScrapIntoTrash(memberDTO, scrapId);

        return ResponseEntity.ok(new ResponseDTO<>());
    }

    /**
     * [PATCH] /scraps/trash
     * [API-18] 스크랩 삭제 (목록)
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PatchMapping("/trash")
    public ResponseEntity<ResponseDTO<Void>> scrapListRemove(@RequestHeader("Authorization") String token,
                                                       @RequestBody @Validated ScrapRequest.DeleteScrapListDTO request){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        scrapCommandService.throwScrapListIntoTrash(memberDTO, request);

        return ResponseEntity.ok(new ResponseDTO<>());
    }
}
