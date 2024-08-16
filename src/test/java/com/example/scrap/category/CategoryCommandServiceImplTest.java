package com.example.scrap.category;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.data.PolicyData;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.entity.enums.ScrapStatus;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.category.CategoryCommandServiceImpl;
import com.example.scrap.web.category.CategoryRepository;
import com.example.scrap.web.category.ICategoryQueryService;
import com.example.scrap.web.category.dto.CategoryRequest;
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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryCommandServiceImplTest {

    @InjectMocks
    private CategoryCommandServiceImpl categoryCommandService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ICategoryQueryService categoryQueryService;

    @Mock
    private IMemberQueryService memberQueryService;

    @DisplayName("카테고리 생성")
    @Test
    public void createCategory(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);
        CategoryRequest.CreateCategoryDTO createCategoryDTO = new CategoryRequest.CreateCategoryDTO("테스트카테고리");

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryRepository.findMaxSequenceByMember(member)).thenReturn(Optional.of(2));

        //** when
        Category newCategory = categoryCommandService.createCategory(memberDTO, createCategoryDTO);

        //** then
        // 제목 확인
        assertThat(newCategory.getTitle())
                .isEqualTo(createCategoryDTO.getCategoryTitle());
        // sequence 확인
        assertThat(newCategory.getSequence())
                .isEqualTo(3);
    }

    @DisplayName("[에러] 카테고리 생성개수 초과")
    @Test
    public void exceedCategoryCreate(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);
        CategoryRequest.CreateCategoryDTO createCategoryDTO = new CategoryRequest.CreateCategoryDTO("테스트카테고리");

        // 미리 최대 카테고리 개수만큼 생성해둠
        setupCategoryList(member, PolicyData.CATEGORY_CREATE_LIMIT);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);

        //** when
        Throwable throwable = catchThrowable(() -> {
            categoryCommandService.createCategory(memberDTO, createCategoryDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.EXCEED_CATEGORY_CREATE_LIMIT.getMessage());
    }

    @DisplayName("[에러] 사용자 카테고리 중 max sequence를 찾을 수 없음")
    @Test
    public void canNotFoundCategoryMaxSequence(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);
        CategoryRequest.CreateCategoryDTO createCategoryDTO = new CategoryRequest.CreateCategoryDTO("테스트카테고리");

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryRepository.findMaxSequenceByMember(member)).thenReturn(Optional.empty());

        //** when
        Throwable throwable = catchThrowable(() -> {
            categoryCommandService.createCategory(memberDTO, createCategoryDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("기본 카테고리 생성")
    @Test
    public void createDefaultCategory() {
        //** given
        Member member = setupMember();

        //** when
        Category defaultCategory = categoryCommandService.createDefaultCategory(member);

        //** then
        assertThat(defaultCategory.getIsDefault()) // 기본 카테고리인지 확인
                .isTrue();
        assertThat(defaultCategory.getTitle()) // 기본 카테고리명과 일치하는지 확인
                .isEqualTo(PolicyData.DEFAULT_CATEGORY_TITLE);
        assertThat(defaultCategory.getSequence()) // sequence는 맨 처음이어야 함
                .isEqualTo(1);
    }

    @DisplayName("카테고리 삭제 - 스크랩도 삭제")
    @Test
    public void deleteCategoryWithDeleteScrap() {
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        List<Category> testCategoryList = setupCategoryList(member, 2);
        Category defaultCategory = testCategoryList.get(0); // 기본 카테고리
        Category deleteCategory = testCategoryList.get(1); // 삭제할 카테고리

        List<Scrap> scrapList = setupScrapList(member, deleteCategory, 3); // 삭제할 카테고리에 속한 스크랩

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryQueryService.findCategory(deleteCategory.getId())).thenReturn(deleteCategory);
        when(categoryQueryService.findDefaultCategory(member)).thenReturn(defaultCategory);

        //** when
        categoryCommandService.deleteCategory(memberDTO, deleteCategory.getId(), true);

        //** then
        // 모든 스크랩이 삭제됐는지 확인
        for(Scrap scrap : scrapList){
            assertThat(scrap.getStatus()) // 스크랩 상태 확인
                    .isEqualTo(ScrapStatus.TRASH);
            assertThat(scrap.getCategory()) // 기본 카테고리로 이동했는지 확인
                    .isEqualTo(defaultCategory);
            assertThat(scrap.getTrashedAt()) // 휴지통으로 보내진 날짜 설정됐는지 확인
                    .isNotNull();
        }

        // delete()가 1번 호출됐는지 확인
        verify(categoryRepository, times(1)).delete(deleteCategory);
    }

    @DisplayName("카테고리 삭제 - 스크랩 삭제 X")
    @Test
    public void deleteCategory() {
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        List<Category> testCategoryList = setupCategoryList(member, 2);
        Category defaultCategory = testCategoryList.get(0); // 기본 카테고리
        Category deleteCategory = testCategoryList.get(1); // 삭제할 카테고리

        List<Scrap> scrapList = setupScrapList(member, deleteCategory, 3); // 삭제할 카테고리에 속한 스크랩

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryQueryService.findCategory(deleteCategory.getId())).thenReturn(deleteCategory);
        when(categoryQueryService.findDefaultCategory(member)).thenReturn(defaultCategory);

        //** when
        categoryCommandService.deleteCategory(memberDTO, deleteCategory.getId(), false);

        //** then
        // 모든 스크랩이 보존되어 있는지 확인
        for(Scrap scrap : scrapList){
            assertThat(scrap.getStatus()) // 스크랩 상태 확인
                    .isEqualTo(ScrapStatus.ACTIVE);
            assertThat(scrap.getCategory()) // 기본 카테고리로 이동했는지 확인
                    .isEqualTo(defaultCategory);
        }

        // delete()가 1번 호출됐는지 확인
        verify(categoryRepository, times(1)).delete(deleteCategory);
    }

    @DisplayName("[에러] 삭제하려는 카테고리와 사용자가 일치하지 않음")
    @Test
    public void deleteCategoryNotMatchToMember(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);
        Member otherMember = Member.builder().build();

        Category deleteCategory = setupCategory(otherMember, false); // 삭제할 카테고리
        ReflectionTestUtils.setField(deleteCategory, "id", 99L);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryQueryService.findCategory(deleteCategory.getId())).thenReturn(deleteCategory);

        //** when
        Throwable throwable = catchThrowable(() -> {
            categoryCommandService.deleteCategory(memberDTO, deleteCategory.getId(), false);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.CATEGORY_MEMBER_NOT_MATCH.getMessage());
    }

    @DisplayName("[에러] 기본 카테고리는 삭제할 수 없음")
    @Test
    public void tryDeleteDefaultCategory(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        Category defaultCategory = setupCategory(member, true); // 기본 카테고리
        ReflectionTestUtils.setField(defaultCategory, "id", 88L);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryQueryService.findCategory(defaultCategory.getId())).thenReturn(defaultCategory);

        //** when
        Throwable throwable = catchThrowable(() -> {
            categoryCommandService.deleteCategory(memberDTO, defaultCategory.getId(), false);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.NOT_ALLOW_ACCESS_DEFAULT_CATEGORY.getMessage());
    }

    @DisplayName("모든 카테고리 삭제")
    @Test
    public void deleteAllCategory(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);

        //** when
        categoryCommandService.deleteAllCategory(memberDTO);

        //** then
        verify(categoryRepository).deleteAllByMember(member);
    }

    @DisplayName("카테고리명 수정")
    @Test
    public void modifyCategoryTitle(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);
        CategoryRequest.UpdateCategoryTitleDTO requestDTO = new CategoryRequest.UpdateCategoryTitleDTO("새로운 카테고리명");

        Category category = setupCategory(member, false);
        ReflectionTestUtils.setField(category, "id", 99L);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryQueryService.findCategory(category.getId())).thenReturn(category);

        //** when
        Category modifyCategory = categoryCommandService.updateCategoryTitle(memberDTO, category.getId(), requestDTO);

        //** then
        assertThat(modifyCategory.getTitle())
                .isEqualTo(requestDTO.getNewCategoryTitle());
    }

    @DisplayName("[에러] 카테고리명 수정시, 수정하려는 카테고리와 사용자가 일치하지 않음")
    @Test
    public void modifyCategoryNotMatchToMember(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);
        CategoryRequest.UpdateCategoryTitleDTO requestDTO = new CategoryRequest.UpdateCategoryTitleDTO("새로운 카테고리명");

        Member ohterMember = Member.builder().build();
        Category category = setupCategory(ohterMember, false);
        ReflectionTestUtils.setField(category, "id", 99L);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryQueryService.findCategory(category.getId())).thenReturn(category);

        //** when
        Throwable throwable = catchThrowable(() -> {
            categoryCommandService.updateCategoryTitle(memberDTO, category.getId(), requestDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.CATEGORY_MEMBER_NOT_MATCH.getMessage());
    }

    @DisplayName("[에러] 카테고리명 수정시, 기본 카테고리는 수정 불가")
    @Test
    public void notAllowModifyDefaultCategoryTitle(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);
        CategoryRequest.UpdateCategoryTitleDTO requestDTO = new CategoryRequest.UpdateCategoryTitleDTO("새로운 카테고리명");

        Category defaultCategory = setupCategory(member, true);
        ReflectionTestUtils.setField(defaultCategory, "id", 88L);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryQueryService.findCategory(defaultCategory.getId())).thenReturn(defaultCategory);

        //** when
        Throwable throwable = catchThrowable(() -> {
            categoryCommandService.updateCategoryTitle(memberDTO, defaultCategory.getId(), requestDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.NOT_ALLOW_ACCESS_DEFAULT_CATEGORY.getMessage());
    }

    @DisplayName("카테고리 순서 변경 - 맨 위로 변경")
    @Test
    public void changeCategorySequence_top(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        setupCategoryList(member, 5);
        CategoryRequest.UpdateCategorySequenceDTO requestDTO = new CategoryRequest.UpdateCategorySequenceDTO(
                List.of(3L, 1L, 2L, 4L, 5L) // 이 순서대로 카테고리 순서 변경
        );

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);

        //** when
        List<Category> changeCategoryList = categoryCommandService.updateCategorySequence(memberDTO, requestDTO);

        //** then
        List<Long> changeCategoryIdLit = new ArrayList<>();
        for(Category category : changeCategoryList){
            changeCategoryIdLit.add(category.getId());
        }

        assertThat(changeCategoryIdLit)
                .containsExactly(3L, 1L, 2L, 4L, 5L);
    }

    @DisplayName("카테고리 순서 변경 - 중간 변경")
    @Test
    public void changeCategorySequence_middle(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        setupCategoryList(member, 5);
        CategoryRequest.UpdateCategorySequenceDTO requestDTO = new CategoryRequest.UpdateCategorySequenceDTO(
                List.of(1L, 2L, 4L, 3L, 5L) // 이 순서대로 카테고리 순서 변경
        );

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);

        //** when
        List<Category> changeCategoryList = categoryCommandService.updateCategorySequence(memberDTO, requestDTO);

        //** then
        List<Long> changeCategoryIdLit = new ArrayList<>();
        for(Category category : changeCategoryList){
            changeCategoryIdLit.add(category.getId());
        }

        assertThat(changeCategoryIdLit)
                .containsExactly(1L, 2L, 4L, 3L, 5L);
    }

    @DisplayName("카테고리 순서 변경 - 맨 아래 변경")
    @Test
    public void changeCategorySequence_bottom(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        setupCategoryList(member, 5);
        CategoryRequest.UpdateCategorySequenceDTO requestDTO = new CategoryRequest.UpdateCategorySequenceDTO(
                List.of(1L, 2L, 4L, 5L, 3L) // 이 순서대로 카테고리 순서 변경
        );

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);

        //** when
        List<Category> changeCategoryList = categoryCommandService.updateCategorySequence(memberDTO, requestDTO);

        //** then
        List<Long> changeCategoryIdLit = new ArrayList<>();
        for(Category category : changeCategoryList){
            changeCategoryIdLit.add(category.getId());
        }

        assertThat(changeCategoryIdLit)
                .containsExactly(1L, 2L, 4L, 5L, 3L);
    }

    @DisplayName("[에러] 카테고리 순서 변경 시, 모든 카테고리에 대해 요청하지 않음")
    @Test
    public void notRequestAllCategorySequenceChange(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        setupCategoryList(member, 5);
        CategoryRequest.UpdateCategorySequenceDTO requestDTO = new CategoryRequest.UpdateCategorySequenceDTO(
                List.of(1L, 2L, 4L, 5L) // 이 순서대로 카테고리 순서 변경
        );

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);

        //** when
        Throwable throwable = catchThrowable(() -> {
            categoryCommandService.updateCategorySequence(memberDTO, requestDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.REQUEST_CATEGORY_COUNT_NOT_ALL.getMessage());
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

    /**
     * 카테고리 id 임의 설정 해줌
     */
    private List<Category> setupCategoryList(Member member, int size){
        List<Category> categoryList = new ArrayList<>();

        Category category;
        for(int i=0; i<size; i++){
            category = Category.builder()
                    .title("카테고리" + i)
                    .sequence(i+1)
                    .isDefault(i==0 ? true : false) // 하나는 기본 카테고리로 만들기
                    .member(member)
                    .build();

            ReflectionTestUtils.setField(category, "id", (long) (i + 1));

            categoryList.add(category);
        }

        return categoryList;
    }

    /**
     * 카테고리 id 설정 안해줌
     */
    private Category setupCategory(Member member, boolean isDefault){
        return Category.builder()
                .member(member)
                .title("카테고리명")
                .sequence(1)
                .isDefault(isDefault)
                .build();
    }

    private List<Scrap> setupScrapList(Member member, Category category, int size){
        List<Scrap> scrapList = new ArrayList<>();

        Scrap scrap;
        for(int i=0; i<size; i++){
            scrap = Scrap.builder()
                    .member(member)
                    .category(category)
                    .status(ScrapStatus.ACTIVE)
                    .title("스크랩 제목")
                    .scrapURL("https://scrap")
                    .imageURL("https://image")
                    .memo("메모메모")
                    .description("본문 내용")
                    .isFavorite(i % 2 == 0)
                    .build();

            scrapList.add(scrap);
        }

        return scrapList;
    }
}
