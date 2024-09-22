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

    @DisplayName("[에러] 카테고리 찾기 / 존재하지 않는 카테고리")
    @Test
    public void errorFindCategoryById_notFound(){
        //** given
        Long categoryId = 99L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        //** when
        Throwable throwable = catchThrowable(() -> {
            categoryQueryService.findCategory(categoryId);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.CATEGORY_NOT_FOUND.getCode());
    }

    @DisplayName("[에러] 카테고리 찾기 / 삭제된 카테고리 찾음")
    @Test
    public void errorFindCategoryById_foundDeletedCategory(){
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

    @DisplayName("카테고리 찾기 - 멤버 일치 여부도 검증")
    @Test
    public void findCategory_validateEqualsMember(){
        //** given
        Member member = setupMember();

        Category category = setupCategory(member, false);
        ReflectionTestUtils.setField(category, "id", 1L);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        //** when
        Category findCategory = categoryQueryService.findCategory(category.getId(), member);

        //** then
        assertThat(findCategory.getId())
                .isEqualTo(category.getId());
    }

    @DisplayName("[에러] 카테고리 찾기 - 멤버 일치 여부도 검증 / 삭제된 카테고리 조회")
    @Test
    public void errorFindCategory_validateEqualsMember_foundDeletedCategory(){
        //** given
        Member member = setupMember();

        Category category = setupCategory(member, false);
        category.delete(); // 카테고리 삭제처리하기
        ReflectionTestUtils.setField(category, "id", 1L);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        //** when
        Throwable throwable = catchThrowable(() -> {
            categoryQueryService.findCategory(category.getId(), member);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.DELETED_CATEGORY.getCode());
    }

    @DisplayName("[에러] 카테고리 찾기 - 멤버 일치 여부도 검증 / 카테고리의 멤버와 매개변수 멤버가 일치하지 않음")
    @Test
    public void errorFindCategory_validateEqualsMember_categoryNotMatchToMember(){
        //** given
        Member member = setupMember();
        Member otherMember = Member.builder().build();

        Category category = setupCategory(otherMember, false);
        ReflectionTestUtils.setField(category, "id", 1L);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        //** when
        Throwable throwable = catchThrowable(() -> {
            categoryQueryService.findCategory(category.getId(), member);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.CATEGORY_MEMBER_NOT_MATCH.getCode());
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
