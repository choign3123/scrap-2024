package com.example.scrap.web.category;

import com.example.scrap.base.exception.ValidationException;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.CategoryConverter;
import com.example.scrap.entity.Category;
import com.example.scrap.jwt.ITokenProvider;
import com.example.scrap.web.category.dto.CategoryRequest;
import com.example.scrap.web.category.dto.CategoryResponse;
import com.example.scrap.web.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    private final ICategoryQueryService categoryQueryService;
    private final ICategoryCommandService categoryCommandService;
    private final ITokenProvider tokenProvider;

    /**
     * [POST] /categories
     * [API-7] 카테고리 생성
     */
    @PostMapping()
    public ResponseEntity<ResponseDTO> categorySave(@RequestHeader("Authorization") String token, @RequestBody @Valid CategoryRequest.CreateCategoryDTO request){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        Category newCategory = categoryCommandService.createCategory(memberDTO, request);

        CategoryResponse.CreateCategoryDTO response = CategoryConverter.toCreateCategoryDTO(newCategory);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [GET] /categories
     * [API-6] 카테고리 전체 조회
     */
    @GetMapping()
    public ResponseEntity<ResponseDTO> categoryWholeList(@RequestHeader("Authorization") String token){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        List<Category> categoryList = categoryQueryService.getCategoryWholeList(memberDTO);
        CategoryResponse.GetCategoryListDTO response = CategoryConverter.toGetCategoryListDTO(categoryList);

        return ResponseEntity.ok(new ResponseDTO(response));
    }


    /**
     * [GET] /categories/selection
     * [API-30] 카테고리 선택용 조회
     */
    @GetMapping("/selection")
    public ResponseEntity<ResponseDTO> categoryListForSelection(@RequestHeader("Authorization") String token){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        List<Category> categoryList = categoryQueryService.getCategoryWholeList(memberDTO);
        CategoryResponse.GetCategoryListForSelectionDTO response = CategoryConverter.toGetCategoryListForSelectionDTO(categoryList);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [DELETE] /categories/{category-id}?allow_delete_category=
     * [API-9] 카테고리 삭제
     */
    @DeleteMapping("/{category-id}")
    public ResponseEntity<ResponseDTO> categoryRemove(@RequestHeader("Authorization") String token, @PathVariable("category-id") Long categoryId){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        categoryCommandService.deleteCategory(memberDTO, categoryId);

        return ResponseEntity.ok(new ResponseDTO());
    }

    /** [PATCH] /categories/{category-id}/title
     * [API-10] 카테고리명 수정
     */
    @PatchMapping("/{category-id}/title")
    public ResponseEntity<ResponseDTO> categoryTitleModify(@RequestHeader("Authorization") String token,
                                                           @PathVariable("category-id") Long categoryId,
                                                           @RequestBody @Valid CategoryRequest.UpdateCategoryTitleDTO request){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        Category category = categoryCommandService.updateCategoryTitle(memberDTO, categoryId, request);
        CategoryResponse.UpdateCategoryTitleDTO response = CategoryConverter.toUpdateCategoryTitleDTO(category);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [PATCH] /categories/sequence
     * [API-8] 카테고리 순서 변경
     */
    @PatchMapping("/sequence")
    public ResponseEntity<ResponseDTO> categorySequenceModify(@RequestHeader("Authorization") String token, @RequestBody @Validated CategoryRequest.UpdateCategorySequenceDTO request){

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        // 중복된 카테고리 있는지 확인
        boolean includeDuplicateCategory = (request.getCategoryIdList().size() != request.getCategoryIdList().stream().distinct().count());
        if(includeDuplicateCategory){
            throw new ValidationException("categories", "중복된 카테고리를 포함하고 있습니다.");
        }

        List<Category> categoryList = categoryCommandService.updateCategorySequence(memberDTO, request);
        CategoryResponse.UpdateCategorySequenceDTO response = CategoryConverter.toUpdateCategorySequenceDTO(categoryList);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

}
