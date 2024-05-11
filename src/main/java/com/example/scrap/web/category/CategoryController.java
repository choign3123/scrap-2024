package com.example.scrap.web.category;

import com.example.scrap.base.response.ApiResponse;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.CategoryConverter;
import com.example.scrap.entity.Category;
import com.example.scrap.validation.annotaion.ExistCategory;
import com.example.scrap.web.category.dto.CategoryRequest;
import com.example.scrap.web.category.dto.CategoryResponse;
import com.example.scrap.web.member.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
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

        Category newCategory = categoryService.createCategory(memberDTO, request);

        CategoryResponse.CreateCategoryDTO response = CategoryConverter.toCreateCategoryDTO(newCategory);

        return new ApiResponse(new ResponseDTO<>(response));
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


    /**
     * [GET] /categories/selection
     * [API-30] 카테고리 선택용 조회
     * @param memberId
     * @return
     */
    @GetMapping("/selection")
    public ApiResponse categoryListForSelection(@RequestHeader("member-id") Long memberId){

        MemberDTO memberDTO = new MemberDTO(memberId);

        List<Category> categoryList = categoryService.getCategoryWholeList(memberDTO);
        CategoryResponse.GetCategoryListForSelectionDTO response = CategoryConverter.toGetCategoryListForSelectionDTO(categoryList);

        return new ApiResponse(new ResponseDTO<>(response));
    }

    /**
     * [DELETE] /categories/{category-id}?allow_delete_category=
     * [API-9] 카테고리 삭제
     * @param memberId
     * @param categoryId
     * @param allowDeleteScrap 해당 카테고리에 속한 스크랩 삭제 여부
     * @return
     */
    @DeleteMapping("/{category-id}")
    public ApiResponse categoryRemove(@RequestHeader("member-id") Long memberId, @PathVariable("category-id") @ExistCategory Long categoryId, @RequestParam("allow_delete_scrap") Boolean allowDeleteScrap){

        MemberDTO memberDTO = new MemberDTO(memberId);

        categoryService.deleteCategory(memberDTO, categoryId, allowDeleteScrap);

        return new ApiResponse(new ResponseDTO<Void>());
    }

    /** [PATCH] /categories/{category-id}/title
     * [API-10] 카테고리명 수정
     * @param memberId
     * @param categoryId 카테고리 식별자
     * @param request
     * @return
     */
    @PatchMapping("/{category-id}/title")
    public ApiResponse categoryTitleModify(@RequestHeader("member-id") Long memberId, @PathVariable("category-id") @ExistCategory Long categoryId, @RequestBody @Valid CategoryRequest.UpdateCategoryTitleDTO request){

        MemberDTO memberDTO = new MemberDTO(memberId);

        Category category = categoryService.updateCategoryTitle(memberDTO, categoryId, request);
        CategoryResponse.UpdateCategoryTitleDTO response = CategoryConverter.toUpdateCategoryTitleDTO(category);

        return new ApiResponse(new ResponseDTO<>(response));
    }

    /**
     * [PATCH] /categories/sequence
     * [API-8] 카테고리 순서 변경
     * @param memberId
     * @param request
     * @return
     */
    @PatchMapping("/sequence")
    public ApiResponse categorySequenceModify(@RequestHeader("member-id") Long memberId, @RequestBody @Validated CategoryRequest.UpdateCategorySequenceDTO request){

        MemberDTO memberDTO = new MemberDTO(memberId);

        List<Category> categoryList = categoryService.updateCategorySequence(memberDTO, request);
        CategoryResponse.UpdateCategorySequenceDTO response = CategoryConverter.toUpdateCategorySequenceDTO(categoryList);

        return new ApiResponse(new ResponseDTO<>(response));
    }

}
