package com.example.scrap.category;

import com.example.scrap.base.exception.GlobalExceptionHandler;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.ITokenProvider;
import com.example.scrap.web.category.CategoryController;
import com.example.scrap.web.category.ICategoryCommandService;
import com.example.scrap.web.category.ICategoryQueryService;
import com.example.scrap.web.category.dto.CategoryRequest.*;
import com.example.scrap.web.member.dto.MemberDTO;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private ICategoryQueryService categoryQueryService;

    @Mock
    private ICategoryCommandService categoryCommandService;

    @Mock
    private ITokenProvider tokenProvider;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
    }

    @DisplayName("카테고리 생성")
    @Test
    public void categorySave() throws Exception{
        //** given
        String token = setupToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // request 설정
        CreateCategoryDTO request = new CreateCategoryDTO("수능");

        // 카테고리 설정
        Category category = Category.builder()
                .member(member)
                .title(request.getCategoryTitle())
                .sequence(1)
                .isDefault(false)
                .build();

        when(tokenProvider.parseAccessToMemberDTO(any(String.class))).thenReturn(memberDTO);
        when(categoryCommandService.createCategory(any(MemberDTO.class), any(CreateCategoryDTO.class))).thenReturn(category);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.title").value(category.getTitle()));
    }

    @DisplayName("[에러] 카테고리 생성 / 카테고리 제목 미입력")
    @Test
    public void errorCategorySave_titleBlank() throws Exception{
        //** given
        String token = setupToken();
        Member member = setupMember();

        // request 설정
        CreateCategoryDTO request = new CreateCategoryDTO("");

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 카테고리 생성 / 카테고리 제목 null")
    @Test
    public void errorCategorySave_titleNull() throws Exception{
        //** given
        String token = setupToken();
        Member member = setupMember();

        // request 설정
        CreateCategoryDTO request = new CreateCategoryDTO(null);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("카테고리 전체 조회")
    @Test
    public void categoryWholeList() throws Exception{
        //** given
        String token = setupToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        List<Category> categoryList = new ArrayList<>();
        for(int i=0; i<5; i++){
            categoryList.add(
                    Category.builder()
                            .member(member)
                            .title("제목" + i)
                            .sequence(i+1)
                            .isDefault(false)
                            .build()
            ) ;
        }

        when(tokenProvider.parseAccessToMemberDTO(any(String.class))).thenReturn(memberDTO);
        when(categoryQueryService.getCategoryWholeList(memberDTO)).thenReturn(categoryList);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.total").value(categoryList.size()));
    }

    @DisplayName("카테고리 선택용 조회")
    @Test
    public void categoryListForSelection() throws Exception{
        //** given
        String token = setupToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        List<Category> categoryList = new ArrayList<>();
        for(int i=0; i<5; i++){
            categoryList.add(
                    Category.builder()
                            .member(member)
                            .title("제목" + i)
                            .sequence(i+1)
                            .isDefault(i==0)
                            .build()
            ) ;
        }

        when(tokenProvider.parseAccessToMemberDTO(any(String.class))).thenReturn(memberDTO);
        when(categoryQueryService.getCategoryWholeList(memberDTO)).thenReturn(categoryList);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/categories/selection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.total").value(categoryList.size()));
    }

    @DisplayName("카테고리 삭제")
    @Test
    public void categoryRemove() throws Exception{
        //** given
        String token = setupToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        when(tokenProvider.parseAccessToMemberDTO(any(String.class))).thenReturn(memberDTO);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/auth/categories/{category-id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("카테고리명 수정")
    @Test
    public void categoryTitleModify() throws Exception{
        //** given
        String token = setupToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // request 설정
        UpdateCategoryTitleDTO request = new UpdateCategoryTitleDTO("수정된 카테고리");

        // 카테고리 설정
        Category category = Category.builder()
                .member(member)
                .title(request.getNewCategoryTitle())
                .sequence(1)
                .build();
        ReflectionTestUtils.setField(category, "id", 99L);

        when(tokenProvider.parseAccessToMemberDTO(any(String.class))).thenReturn(memberDTO);
        when(categoryCommandService.updateCategoryTitle(eq(memberDTO), eq(category.getId()), any(UpdateCategoryTitleDTO.class))).thenReturn(category);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/categories/{category-id}/title", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.newCategoryTitle").value(request.getNewCategoryTitle()))
                .andDo(print());
    }

    @DisplayName("[에러] 카테고리명 수정 / 새로운 카테고리명 입력 안함")
    @Test
    public void errorCategoryTitleModify_titleBlank() throws Exception{
        //** given
        String token = setupToken();

        // request 설정
        UpdateCategoryTitleDTO request = new UpdateCategoryTitleDTO("");

        // 카테고리 설정
        Long categoryId = 99L;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/categories/{category-id}/title", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @DisplayName("[에러] 카테고리명 수정 / 새로운 카테고리명에 null 입력")
    @Test
    public void errorCategoryTitleModify_titleNull() throws Exception{
        //** given
        String token = setupToken();

        // request 설정
        UpdateCategoryTitleDTO request = new UpdateCategoryTitleDTO(null);

        // 카테고리 설정
        Long categoryId = 99L;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/categories/{category-id}/title", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @DisplayName("카테고리 순서 변경")
    @Test
    public void categorySequenceModify() throws Exception{
        //** given
        String token = setupToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // request 설정
        UpdateCategorySequenceDTO request = new UpdateCategorySequenceDTO(List.of(5L, 4L, 3L, 2L, 1L));

        // 카테고리 설정
        List<Category> categoryList = new ArrayList<>();
        for(int i=0; i<5; i++){
            categoryList.add(
                    Category.builder()
                            .member(member)
                            .title("제목" + i)
                            .sequence(i+1)
                            .isDefault(i==0)
                            .build()
            ) ;
        }

        when(tokenProvider.parseAccessToMemberDTO(any(String.class))).thenReturn(memberDTO);
        when(categoryCommandService.updateCategorySequence(eq(memberDTO), any(UpdateCategorySequenceDTO.class))).thenReturn(categoryList);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/categories/sequence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.total").value(request.getCategoryIdList().size()));
    }

    @DisplayName("[에러] 카테고리 순서 변경 / 카테고리 미입력")
    @Test
    public void errorCategorySequenceModify_categoryEmpty() throws Exception{
        //** given
        String token = setupToken();

        // request 설정
        UpdateCategorySequenceDTO request = new UpdateCategorySequenceDTO(new ArrayList<>());

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/categories/sequence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 카테고리 순서 변경 / 카테고리 null 입력")
    @Test
    public void errorCategorySequenceModify_categoryNull() throws Exception{
        //** given
        String token = setupToken();

        // request 설정
        UpdateCategorySequenceDTO request = new UpdateCategorySequenceDTO(null);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/categories/sequence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 카테고리 순서 변경 / 중복된 카테고리 입력")
    @Test
    public void errorCategorySequenceModify_duplicateCategory() throws Exception{
        //** given
        String token = setupToken();

        // request 설정
        UpdateCategorySequenceDTO request = new UpdateCategorySequenceDTO(List.of(5L, 5L, 3L, 2L, 1L));

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/categories/sequence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    private String setupToken(){
        return "memberId:1,snsId:gana,snsType:NAVER";
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
}
