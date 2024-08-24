package com.example.scrap.category;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.CategoryStatus;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.category.CategoryQueryServiceImpl;
import com.example.scrap.web.category.CategoryRepository;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryQueryServiceImplTest {

    @InjectMocks
    private CategoryQueryServiceImpl categoryQueryService;

    @Mock
    private IMemberQueryService memberQueryService;

    @Mock
    private CategoryRepository categoryRepository;


    @DisplayName("카테고리 전체 조회")
    @Test
    public void getWholeCategoryList(){
        // given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);
        List<Category> categoryList = createCategoryList(member);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryRepository.findAllByMemberAndStatusOrderBySequence(member, CategoryStatus.ACTIVE)).thenReturn(categoryList);

        // when
        final List<Category> findCategoryList = categoryQueryService.getCategoryWholeList(memberDTO);

        // then
        assertThat(findCategoryList.size())
                .isEqualTo(categoryList.size());
    }

    @DisplayName("카테고리 찾기")
    @Test
    public void findCategoryById(){
        //** given
        Category category = setupCategory(setupMember(), false);
        ReflectionTestUtils.setField(category, "id", 1L);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        //** when
        Category findCategory = categoryQueryService.findCategory(category.getId());

        //** then
        assertThat(findCategory.getId())
                .isEqualTo(category.getId());
    }

    @DisplayName("[에서] 카테고리 찾기 / 삭제된 카테고리 찾음")
    @Test
    public void errorFindCategoryById_foundDeleteCategory(){
        //** given
        Category category = setupCategory(setupMember(), false);
        category.delete();
        ReflectionTestUtils.setField(category, "id", 1L);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        //** when
        Throwable throwable = catchThrowable(() -> {
            categoryQueryService.findCategory(category.getId());
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.DELETED_CATEGORY.getCode());
    }

    private Member setupMember(){
        return Member.builder()
                .name("홍길동")
                .snsId("testSnsId")
                .snsType(SnsType.NAVER)
                .build();
    }

    private MemberDTO setupMemberDTO(Member member){
        return MemberDTO.builder()
                .memberId(member.getId())
                .snsId(member.getSnsId())
                .snsType(member.getSnsType())
                .build();
    }

    private List<Category> createCategoryList(Member member){
        List<Category> categoryList = new ArrayList<>();

        Category category;
        for(int i=0; i<10; i++){
            category = Category.builder()
                    .title("카테고리" + i)
                    .sequence(i+1)
                    .isDefault(i==0 ? true : false) // 하나는 기본 카테고리로 만들기
                    .member(member)
                    .build();

            categoryList.add(category);
        }

        return categoryList;
    }

    private Category setupCategory(Member member, boolean isDefault){
        return Category.builder()
                .member(member)
                .title("카테고리명")
                .sequence(1)
                .isDefault(isDefault)
                .build();
    }
}
