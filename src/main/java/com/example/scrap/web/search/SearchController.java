package com.example.scrap.web.search;

import com.example.scrap.base.exception.ValidationException;
import com.example.scrap.base.response.ApiResponse;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.SearchConverter;
import com.example.scrap.entity.Scrap;
import com.example.scrap.validation.annotaion.EnumValid;
import com.example.scrap.validation.annotaion.PagingPage;
import com.example.scrap.validation.annotaion.PagingSize;
import com.example.scrap.web.baseDTO.Data;
import com.example.scrap.web.baseDTO.Sorts;
import com.example.scrap.web.member.MemberDTO;
import com.example.scrap.web.search.dto.SearchRequest;
import com.example.scrap.web.search.dto.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SearchController {

    private final ISearchService searchService;

    /**
     * [POST] /search
     * [API-22] 스크랩 검색하기
     * @param memberId
     * @param request
     * @param sort
     * @param direction
     * @param page
     * @param size
     * @param query
     * @return
     */
    @PostMapping
    public ApiResponse scrapSearch(@RequestHeader("member-id") Long memberId, @RequestBody @Validated SearchRequest.FindScrapDTO request,
                                   @RequestParam(name = "sort", defaultValue = "SCRAP_DATE") @EnumValid(enumC = Sorts.class) String sort,
                                   @RequestParam(name = "direction", defaultValue = "ASC") @EnumValid(enumC = Sort.Direction.class) String direction,
                                   @RequestParam(name = "page", defaultValue = "1") @PagingPage int page,
                                   @RequestParam(name = "size", defaultValue = Data.PAGING_SIZE) @PagingSize int size,
                                   @RequestParam(name = "q") @NotBlank String query){

        MemberDTO memberDTO = new MemberDTO(memberId);

        /** 시작, 종료 날짜 검증 **/
        if(request.getStartDate() == null){
            request.setStartDateToDefault();
        }
        if(request.getEndDate() == null){
            request.setEndDateToDefault();
        }
        if(request.getStartDate().isAfter(request.getEndDate())){
            throw new ValidationException("endDate", "종료 날짜가 시작 날짜보다 클 수 없습니다.");
        }
        /** 시작, 종료 날짜 검증 끝 **/

        log.info("query: {}", query);

        // string -> enum 변경
        Sorts sortsEnum = Sorts.valueOf(sort.toUpperCase());
        Sort.Direction directionEnum = Sort.Direction.valueOf(direction.toUpperCase());
        // 페이지네이션
        PageRequest pageRequest = PageRequest.of(page-1, size, directionEnum, sortsEnum.getName());

        Page<Scrap> scrapPage = searchService.findScrap(memberDTO, request, pageRequest, query);
        SearchResponse.FindScrapDTO response = SearchConverter.toFindScrapDTO(scrapPage);

        return new ApiResponse(new ResponseDTO(response));
    }
}
