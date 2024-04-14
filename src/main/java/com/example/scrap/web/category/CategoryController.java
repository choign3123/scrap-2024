package com.example.scrap.web.category;

import com.example.scrap.base.response.ApiResponse;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.entity.Member;
import com.example.scrap.web.category.dto.CategoryRequest;
import com.example.scrap.web.member.IMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final IMemberService memberService;
    private final ICategoryService categoryService;

    @PostMapping()
    public ApiResponse categorySave(@RequestHeader("member-id") Long memberId, @RequestBody @Valid CategoryRequest.CreateCategoryDTO request){

        Member member = memberService.findMember(memberId);
        
        categoryService.createCategory(member, request);

        return new ApiResponse(new ResponseDTO<Void>());
    }
}
