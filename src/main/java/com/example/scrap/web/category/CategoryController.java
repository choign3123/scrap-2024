package com.example.scrap.web.category;

import com.example.scrap.base.response.ApiResponse;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.CategoryConverter;
import com.example.scrap.entity.Category;
import com.example.scrap.web.category.dto.CategoryRequest;
import com.example.scrap.web.category.dto.CategoryResponse;
import com.example.scrap.web.member.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    /**
     * [POST] /categories
     * [API-7] 카테고리 생성
     * @param memberId
     * @param request
     * @return
     */
    @PostMapping()
    public ApiResponse categorySave(@RequestHeader("member-id") Long memberId, @RequestBody @Valid CategoryRequest.CreateCategoryDTO request){

        MemberDTO memberDTO = new MemberDTO(memberId);

        categoryService.createCategory(memberDTO, request);

        return new ApiResponse(new ResponseDTO<Void>());
    }

    /**
     * [GET] /categories
     * [API-6] 카테고리 전체 조회
     * @param memberId
     * @return
     */
    @GetMapping()
    public ApiResponse categoryWholeList(@RequestHeader("member-id") Long memberId){

        MemberDTO memberDTO = new MemberDTO(memberId);

        List<Category> categoryList = categoryService.getCategoryWholeList(memberDTO);
        CategoryResponse.GetCategoryListDTO response = CategoryConverter.toGetCategoryListDTO(categoryList);

        return new ApiResponse(new ResponseDTO<>(response));
    }
}
