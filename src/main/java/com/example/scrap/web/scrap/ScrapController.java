package com.example.scrap.web.scrap;

import com.example.scrap.base.response.ApiResponse;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.ScrapConverter;
import com.example.scrap.entity.Scrap;
import com.example.scrap.validation.annotaion.ExistCategory;
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

    @GetMapping("/{category-id}")
    public ApiResponse scrapListByCategory(@RequestHeader("member-id") Long memberId, @PathVariable("category-id") @ExistCategory Long categoryId,
                                           @RequestParam(name = "sort", defaultValue = "SCRAP_DATE") Sort sort,
                                           @RequestParam(name = "direction", defaultValue = "ASC") Direction direction,
                                           @RequestParam(name = "page", defaultValue = "1") int page,
                                           @RequestParam(name = "size", defaultValue = Data.PAGING_SIZE) int size){

        MemberDTO memberDTO = new MemberDTO(memberId);
        log.info("sort: {}, direction: {}, page: {}, size: {}", sort, direction, page, size);
        PageRequest pageRequest = PageRequest.of(page-1, size, direction, sort.getName());

        Page<Scrap> scrapPage = scrapService.getScrapListByCategory(memberDTO, categoryId, sort, pageRequest);

        ScrapResponse.GetScrapListByCategory response = ScrapConverter.toGetScrapListByCategory(scrapPage);

        return new ApiResponse(new ResponseDTO(response));
    }
}
