package com.example.scrap.web.scrap;

import com.example.scrap.base.response.ApiResponse;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.ScrapConverter;
import com.example.scrap.entity.Scrap;
import com.example.scrap.validation.annotaion.*;
import com.example.scrap.web.baseDTO.Data;
import com.example.scrap.web.baseDTO.Sort;
import com.example.scrap.web.member.MemberDTO;
import com.example.scrap.web.scrap.dto.ScrapRequest;
import com.example.scrap.web.scrap.dto.ScrapResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort.Direction;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
    public ApiResponse scrapSave(@RequestHeader("member-id") Long memberId, @PathVariable("category-id") @ExistCategory Long categoryId, @RequestBody @Valid ScrapRequest.CreateScrap request){
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
                                           @RequestParam(name = "sort", defaultValue = "SCRAP_DATE") @EnumValid(enumC = Sort.class) String sort,
                                           @RequestParam(name = "direction", defaultValue = "ASC") @EnumValid(enumC = Direction.class) String direction,
                                           @RequestParam(name = "page", defaultValue = "1") @PagingPage int page,
                                           @RequestParam(name = "size", defaultValue = Data.PAGING_SIZE) @PagingSize int size){

        MemberDTO memberDTO = new MemberDTO(memberId);

        log.info("sort: {}, direction: {}, page: {}, size: {}", sort, direction, page, size);

        // string -> enum 변경
        Sort sortEnum = Sort.valueOf(sort.toUpperCase());
        Direction directionEnum = Direction.valueOf(direction.toUpperCase());

        // 페이지네이션
        PageRequest pageRequest = PageRequest.of(page-1, size, directionEnum, sortEnum.getName());

        Page<Scrap> scrapPage = scrapService.getScrapListByCategory(memberDTO, categoryId, pageRequest);

        ScrapResponse.GetScrapListByCategory response = ScrapConverter.toGetScrapListByCategory(scrapPage);

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
    public ApiResponse scrapDetails(@RequestHeader("member-id") Long memberId, @PathVariable("scrap-id") @ExistScrap Long scrapId){
        MemberDTO memberDTO = new MemberDTO(memberId);

        Scrap scrap = scrapService.getScrapDetails(memberDTO, scrapId);

        ScrapResponse.GetScrapDetails response = ScrapConverter.toGetScrapDetails(scrap);

        return new ApiResponse(new ResponseDTO<>(response));
    }
}
