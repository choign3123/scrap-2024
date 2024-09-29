package com.example.scrap.scrap;


import com.example.scrap.base.enums.Sorts;
import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.interceptor.AuthorizationInterceptor;
import com.example.scrap.jwt.ITokenProvider;
import com.example.scrap.redis.ILogoutBlacklistRedisUtils;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.scrap.IScrapCommandService;
import com.example.scrap.web.scrap.IScrapQueryService;
import com.example.scrap.web.scrap.ScrapController;
import com.example.scrap.web.scrap.dto.ScrapRequest.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScrapController.class)
public class ScrapControllerTest {

    @MockBean
    private IScrapQueryService scrapQueryService;

    @MockBean
    private IScrapCommandService scrapCommandService;

    @MockBean
    private ITokenProvider tokenProvider;

    @MockBean
    private IMemberQueryService memberQueryService;

    @MockBean
    private ILogoutBlacklistRedisUtils logoutBlacklistRedisUtils;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private AuthorizationInterceptor authorizationInterceptor;

    @Autowired
    private MockMvc mockMvc;

    // AuthorizationInterceptor 넘어가기
    @BeforeEach
    public void passAuthorizationInterceptor(){
        when(authorizationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @DisplayName("스크랩 생성")
    @Test
    public void scrapSave() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Long categoryId = 99L;

        // request 설정
        CreateScrapDTO request = CreateScrapDTO.builder()
                .scrapURL("https://scrap")
                .title("마루는 강쥐")
                .memo("웹툰 너무 재밌다")
                .isFavorite(true)
                .description("네이버 웹툰")
                .imageURL("https://image")
                .build();

        // 스크랩 설정
        Scrap scrap = Scrap.builder()
                .scrapURL(request.getScrapURL())
                .isFavorite(request.getIsFavorite())
                .description(request.getDescription())
                .title(request.getTitle())
                .memo(request.getMemo())
                .imageURL(request.getImageURL())
                .member(member)
                .category(setupCategory(member))
                .build();
        ReflectionTestUtils.setField(scrap, "id", 999L);
        ReflectionTestUtils.setField(scrap, "createdAt", LocalDateTime.now());

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);
        when(scrapCommandService.createScrap(isA(MemberDTO.class), isA(Long.class), isA(CreateScrapDTO.class))).thenReturn(scrap);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/scraps/{category-id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.title").value(request.getTitle()))
                .andExpect(jsonPath("$.result.scrapURL").value(request.getScrapURL()))
                .andExpect(jsonPath("$.result.imageURL").value(request.getImageURL()))
                .andExpect(jsonPath("$.result.isFavorite").value(request.getIsFavorite()))
                .andExpect(jsonPath("$.result.scrapId").exists())
                .andExpect(jsonPath("$.result.scrapDate").exists());
    }

    @DisplayName("[에러] 스크랩 생성 / request body: scrapURL이 빈문자열")
    @Test
    public void errorScrapSave_scrapUrlBlack() throws Exception{
        //** given
        String token = setupStringToken();

        // 카테고리 설정
        Long categoryId = 99L;

        // request 설정
        CreateScrapDTO request = CreateScrapDTO.builder()
                .scrapURL("") // 빈 문자열
                .title("마루는 강쥐")
                .memo("웹툰 너무 재밌다")
                .isFavorite(true)
                .description("네이버 웹툰")
                .imageURL("https://image")
                .build();

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/scraps/{category-id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 생성 / request body: scrapURL이 null")
    @Test
    public void errorScrapSave_scrapUrlNull() throws Exception{
        //** given
        String token = setupStringToken();

        // 카테고리 설정
        Long categoryId = 99L;

        // request 설정
        CreateScrapDTO request = CreateScrapDTO.builder()
                .scrapURL(null) // 빈 문자열
                .title("마루는 강쥐")
                .memo("웹툰 너무 재밌다")
                .isFavorite(true)
                .description("네이버 웹툰")
                .imageURL("https://image")
                .build();

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/scraps/{category-id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 생성 / request body: title이 빈문자열")
    @Test
    public void errorScrapSave_titleBlack() throws Exception{
        //** given
        String token = setupStringToken();

        // 카테고리 설정
        Long categoryId = 99L;

        // request 설정
        CreateScrapDTO request = CreateScrapDTO.builder()
                .scrapURL("https://scrap") // 빈 문자열
                .title("")
                .memo("웹툰 너무 재밌다")
                .isFavorite(true)
                .description("네이버 웹툰")
                .imageURL("https://image")
                .build();

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/scraps/{category-id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 생성 / request body: title이 null")
    @Test
    public void errorScrapSave_titleNull() throws Exception{
        //** given
        String token = setupStringToken();

        // 카테고리 설정
        Long categoryId = 99L;

        // request 설정
        CreateScrapDTO request = CreateScrapDTO.builder()
                .scrapURL("https://scrap") // 빈 문자열
                .title(null)
                .memo("웹툰 너무 재밌다")
                .isFavorite(true)
                .description("네이버 웹툰")
                .imageURL("https://image")
                .build();

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/scraps/{category-id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("스크랩 전체 조회-카테고리별")
    @Test
    public void scrapListByCategory() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // 스크랩 설정
        List<Scrap> scrapList = setupScrap(member, category, 5);
        Page<Scrap> scrapPage = new PageImpl<>(scrapList);

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 10;

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);
        when(scrapQueryService.getScrapListByCategory(isA(MemberDTO.class), isA(Long.class), isA(PageRequest.class))).thenReturn(scrapPage);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("category", category.getId().toString())
                        .param("sort", sorts.name())
                        .param("direction", direction.name())
                        .param("page", page.toString())
                        .param("size", size.toString())
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.meta.numOfElement").value(scrapList.size()));
    }

    @DisplayName("[에러] 스크랩 전체 조회-카테고리별 / query string: sort가 이상한 값")
    @Test
    public void errorScrapListByCategory_sortWrongValue() throws Exception{

        //** given
        String token = setupStringToken();
        Member member = setupMember();

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // query string 설정
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 10;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("category", category.getId().toString())
                        .param("sort", "이상한 값") // sort에 이상한 값 넣음
                        .param("direction", direction.name())
                        .param("page",page.toString())
                        .param("size", size.toString())
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 전체 조회-카테고리별 / query string: direction이 이상한 값")
    @Test
    public void errorScrapListByCategory_directionWrongValue() throws Exception{

        //** given
        String token = setupStringToken();
        Member member = setupMember();

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
//        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 10;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("category", category.getId().toString())
                        .param("sort", sorts.getName())
                        .param("direction", "이상한 값") // direction에 이상한 값 넣음
                        .param("page",page.toString())
                        .param("size", size.toString())
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 전체 조회-카테고리별 / query string: 0 미만의 page값")
    @Test
    public void errorScrapListByCategory_pageUnder0() throws Exception{

        //** given
        String token = setupStringToken();
        Member member = setupMember();

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = -1;
        Integer size = 10;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("category", category.getId().toString())
                        .param("sort", sorts.getName())
                        .param("direction", direction.name()) // direction에 이상한 값 넣음
                        .param("page",page.toString())
                        .param("size", size.toString())
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 전체 조회-카테고리별 / query string: 1 미만의 size 값")
    @Test
    public void errorScrapListByCategory_sizeUnder1() throws Exception{

        //** given
        String token = setupStringToken();
        Member member = setupMember();

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 0;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("category", category.getId().toString())
                        .param("sort", sorts.getName())
                        .param("direction", direction.name()) // direction에 이상한 값 넣음
                        .param("page",page.toString())
                        .param("size", size.toString())
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 전체 조회-카테고리별 / query string: 30 초과의 size값")
    @Test
    public void errorScrapListByCategory_sizeUp30() throws Exception{

        //** given
        String token = setupStringToken();
        Member member = setupMember();

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 31;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("category", category.getId().toString())
                        .param("sort", sorts.getName())
                        .param("direction", direction.name()) // direction에 이상한 값 넣음
                        .param("page",page.toString())
                        .param("size", size.toString())
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("즐겨찾기된 스크랩 조회")
    @Test
    public void favoriteScrapList() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member);

        // 스크랩 설정
        List<Scrap> scrapList = setupScrap(member, category, 5);
        for(Scrap scrap : scrapList){
            scrap.updateFavorite(true); // 모두 즐겨찾기됨으로 설정
        }
        Page<Scrap> scrapPage = new PageImpl<>(scrapList);

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 10;

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);
        when(scrapQueryService.getFavoriteScrapList(isA(MemberDTO.class), isA(PageRequest.class))).thenReturn(scrapPage);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.name())
                        .param("direction", direction.name())
                        .param("page", page.toString())
                        .param("size", size.toString())
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.meta.numOfElement").value(scrapList.size()));
    }

    @DisplayName("[에러] 즐겨찾기된 스크랩 조회 / query string: sort가 이상한 값")
    @Test
    public void errorFavoriteScrapList_sortWrongValue() throws Exception{
        //** given
        String token = setupStringToken();

        // query string 설정
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 10;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", "이상한 값")
                        .param("direction", direction.name())
                        .param("page",page.toString())
                        .param("size", size.toString())
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 즐겨찾기된 스크랩 조회 / query string: direction이 이상한 값")
    @Test
    public void errorFavoriteScrapList_directionWrongValue() throws Exception{
        //** given
        String token = setupStringToken();

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
//        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 10;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.getName())
                        .param("direction", "이상한 값") // direction에 이상한 값 넣음
                        .param("page",page.toString())
                        .param("size", size.toString())
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 즐겨찾기된 스크랩 조회 / query string: 0 미만의 page값")
    @Test
    public void errorFavoriteScrapList_pageUnder0() throws Exception{
        //** given
        String token = setupStringToken();

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = -1;
        Integer size = 10;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.getName())
                        .param("direction", direction.name())
                        .param("page",page.toString())
                        .param("size", size.toString())
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 즐겨찾기된 스크랩 조회 / query string: 1 미만의 size 값")
    @Test
    public void errorFavoriteScrapList_sizeUnder1() throws Exception{
        //** given
        String token = setupStringToken();

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 0;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.getName())
                        .param("direction", direction.name()) // direction에 이상한 값 넣음
                        .param("page",page.toString())
                        .param("size", size.toString())
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 즐겨찾기된 스크랩 조회 / query string: 30 초과의 size값")
    @Test
    public void errorFavoriteScrapList_sizeUp30() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 31;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.getName())
                        .param("direction", direction.name()) // direction에 이상한 값 넣음
                        .param("page",page.toString())
                        .param("size", size.toString())
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("스크랩 세부 조회")
    @Test
    public void scrapDetails() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, category);

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);
        when(scrapQueryService.getScrapDetails(isA(MemberDTO.class), isA(Long.class))).thenReturn(scrap);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/{scrap-id}", scrap.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.title").value(scrap.getTitle()))
                .andExpect(jsonPath("$.result.scrapURL").value(scrap.getScrapURL()))
                .andExpect(jsonPath("$.result.imageURL").value(scrap.getImageURL()))
                .andExpect(jsonPath("$.result.isFavorite").value(scrap.getIsFavorite()))
                .andExpect(jsonPath("$.result.memo").value(scrap.getMemo()))
                .andExpect(jsonPath("$.result.description").value(scrap.getDescription()))
                .andExpect(jsonPath("$.result.scrapId").exists());
    }

    @DisplayName("스크랩 검색 (특정 카테고리에서)")
    @Test
    public void scrapSearchAtParticularCategory() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // 스크랩 설정
        List<Scrap> scrapList = setupScrap(member, category, 5);
        for(Scrap scrap : scrapList){
            scrap.updateMemo("마루");
        }

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        String q = "마루";

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);
        when(scrapQueryService.findScrapAtParticularCategory(isA(MemberDTO.class), isA(Long.class), isA(Sort.class), isA(String.class)))
                .thenReturn(scrapList);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/search/{category-id}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.name())
                        .param("direction", direction.name())
                        .param("q", q)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.total").value(scrapList.size()));
    }

    @DisplayName("[에러] 스크랩 검색 (특정 카테고리에서) / query string: sort가 이상한 값")
    @Test
    public void errorScrapSearchAtParticularCategory_sortWrongValue() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // query string 설정
//        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        String q = "마루";

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/search/{category-id}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", "이상한 값")
                        .param("direction", direction.name())
                        .param("q", q)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 검색 (특정 카테고리에서) / query string: direction이 이상한 값")
    @Test
    public void errorScrapSearchAtParticularCategory_directionWrongValue() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
//        Sort.Direction direction = Sort.Direction.DESC;
        String q = "마루";

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/search/{category-id}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.name())
                        .param("direction", "이상한 값")
                        .param("q", q)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 검색 (특정 카테고리에서) / query string: q가 빈 문자열")
    @Test
    public void errorScrapSearchAtParticularCategory_qBlank() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        String q = "";

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/search/{category-id}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.name())
                        .param("direction", direction.name())
                        .param("q", q)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 검색 (특정 카테고리에서) / query string: q가 null")
    @Test
    public void errorScrapSearchAtParticularCategory_qNull() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        String q = null;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/search/{category-id}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.name())
                        .param("direction", direction.name())
                        .param("q", q)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("스크랩 검색 (즐겨찾기됨에서)")
    @Test
    public void scrapSearchAtFavorite() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // 스크랩 설정
        List<Scrap> scrapList = setupScrap(member, category, 3);
        for(Scrap scrap : scrapList){
            scrap.updateFavorite(true); // 모두 즐겨찾기됨으로 설정하기
            scrap.updateMemo("마루");
        }

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        String q = "마루";

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);
        when(scrapQueryService.findScrapAtFavorite(isA(MemberDTO.class), isA(Sort.class), isA(String.class)))
                .thenReturn(scrapList);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/search/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.name())
                        .param("direction", direction.name())
                        .param("q", q)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.total").value(scrapList.size()));
    }

    @DisplayName("[에러] 스크랩 검색 (즐겨찾기됨에서) / query string: 잘못된 sort값")
    @Test
    public void errorScrapSearchAtFavorite_sortWrongValue() throws Exception{
        //** given
        String token = setupStringToken();

        // query string 설정
//        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        String q = "마루";

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/search/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", "이상한 값")
                        .param("direction", direction.name())
                        .param("q", q)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 검색 (즐겨찾기됨에서) / query string: 잘못된 direction값")
    @Test
    public void errorScrapSearchAtFavorite_directionWrongValue() throws Exception{
        //** given
        String token = setupStringToken();

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
//        Sort.Direction direction = Sort.Direction.DESC;
        String q = "마루";

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/search/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.getName())
                        .param("direction", "이상한 값")
                        .param("q", q)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 검색 (즐겨찾기됨에서) / query string: q가 빈 문자열")
    @Test
    public void errorScrapSearchAtFavorite_qBlank() throws Exception{
        //** given
        String token = setupStringToken();

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        String q = "";

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/search/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.getName())
                        .param("direction", direction.name())
                        .param("q", q)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 검색 (즐겨찾기됨에서) / query string: q가 null")
    @Test
    public void errorScrapSearchAtFavorite_qNull() throws Exception{
        //** given
        String token = setupStringToken();

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        String q = null;

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/scraps/search/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.getName())
                        .param("direction", direction.name())
                        .param("q", q)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("스크랩 즐겨찾기 (단건)")
    @Test
    public void scrapFavoriteToggle() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, category);

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);
        when(scrapCommandService.toggleScrapFavorite(isA(MemberDTO.class), isA(Long.class))).thenReturn(scrap);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/{scrap-id}/favorite", scrap.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.scrapId").value(scrap.getId()))
                .andExpect(jsonPath("$.result.isFavorite").value(scrap.getIsFavorite()));
    }

    @DisplayName("스크랩 즐겨찾기 (목록)")
    @Test
    public void scrapFavoriteListToggle() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 99L);

        // 스크랩 설정
        List<Scrap> scrapList = setupScrap(member, category, 5);

        // request 설정
        List<Long> scrapIdList = scrapList.stream()
                .map(scrap -> scrap.getId())
                .toList();
        ToggleScrapFavoriteListDTO request = new ToggleScrapFavoriteListDTO(scrapIdList);

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);
        when(scrapCommandService.toggleScrapFavoriteList(isA(MemberDTO.class), isA(ToggleScrapFavoriteListDTO.class)))
                .thenReturn(scrapList);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.total").value(scrapList.size()));
    }

    @DisplayName("[에러] 스크랩 즐겨찾기 (목록) / request body: scrapIdList가 빈 리스트")
    @Test
    public void errorScrapFavoriteListToggle_scrapIdListEmpty() throws Exception{
        //** given
        String token = setupStringToken();

        // request 설정
        List<Long> scrapIdList = new ArrayList<>();
        ToggleScrapFavoriteListDTO request = new ToggleScrapFavoriteListDTO(scrapIdList);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 즐겨찾기 (목록) / request body: scrapIdList가 null")
    @Test
    public void errorScrapFavoriteListToggle_scrapIdListNull() throws Exception{
        //** given
        String token = setupStringToken();

        // request 설정
        List<Long> scrapIdList = null;
        ToggleScrapFavoriteListDTO request = new ToggleScrapFavoriteListDTO(scrapIdList);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("스크랩 이동하기 (단건)")
    @Test
    public void categoryOfScrapMove() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category moveCategory = setupCategory(member);
        ReflectionTestUtils.setField(moveCategory, "id", 98L);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, moveCategory);

        // request 설정
        MoveCategoryOfScrapDTO request = new MoveCategoryOfScrapDTO(moveCategory.getId());

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);
        when(scrapCommandService.moveCategoryOfScrap(isA(MemberDTO.class), isA(Long.class), isA(MoveCategoryOfScrapDTO.class))).thenReturn(scrap);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/{scrap-id}/move", scrap.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.scrapId").value(scrap.getId()))
                .andExpect(jsonPath("$.result.categoryId").value(scrap.getCategory().getId()));
    }

    @DisplayName("[에러] 스크랩 이동하기 (단건) / request body: moveCategoryId가 null")
    @Test
    public void errorCategoryOfScrapMove_moveCategoryIdNull() throws Exception{
        //** given
        String token = setupStringToken();

        // 스크랩 설정
        Long scrapId = 999L;

        // request 설정
        MoveCategoryOfScrapDTO request = new MoveCategoryOfScrapDTO(null);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/{scrap-id}/move", scrapId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("스크랩 이동하기 (목록)")
    @Test
    public void categoryOfScrapListMove() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category moveCategory = setupCategory(member);
        ReflectionTestUtils.setField(moveCategory, "id", 98L);

        // 스크랩 설정
        List<Scrap> scrapList = setupScrap(member, moveCategory, 5);

        // request 설정
        List<Long> scrapIdList = scrapList.stream()
                .map(scrap -> scrap.getId())
                .toList();
        MoveCategoryOfScrapsDTO request = new MoveCategoryOfScrapsDTO(scrapIdList, moveCategory.getId());

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);
        when(scrapCommandService.moveCategoryOfScrapList(isA(MemberDTO.class), isA(MoveCategoryOfScrapsDTO.class)))
                .thenReturn(scrapList);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.total").value(scrapList.size()));
    }

    @DisplayName("[에러] 스크랩 이동하기 (목록) / request body: scrapIdList가 빈 리스트")
    @Test
    public void errorCategoryOfScrapListMove_scrapIdListEmpty() throws Exception{
        //** given
        String token = setupStringToken();

        // 카테고리 설정
        Long moveCategoryId = 98L;

        // request 설정
        List<Long> scrapIdList = new ArrayList<>();
        MoveCategoryOfScrapsDTO request = new MoveCategoryOfScrapsDTO(scrapIdList, moveCategoryId);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 이동하기 (목록) / request body: scrapIdList가 null")
    @Test
    public void errorCategoryOfScrapListMove_scrapIdListNull() throws Exception{
        //** given
        String token = setupStringToken();

        // 카테고리 설정
        Long moveCategoryId = 98L;

        // request 설정
        List<Long> scrapIdList = null;
        MoveCategoryOfScrapsDTO request = new MoveCategoryOfScrapsDTO(scrapIdList, moveCategoryId);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 이동하기 (목록) / request body: moveCategoryId가 null")
    @Test
    public void errorCategoryOfScrapListMove_moveCategoryIdNull() throws Exception{
        //** given
        String token = setupStringToken();

        // 카테고리 설정
        Long moveCategoryId = null;

        // request 설정
        List<Long> scrapIdList = List.of(1L, 2L, 3L);
        MoveCategoryOfScrapsDTO request = new MoveCategoryOfScrapsDTO(scrapIdList, moveCategoryId);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("스크랩의 메모 수정")
    @Test
    public void scrapMemoModify() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 98L);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, category);
        scrap.updateMemo("수정된 메모 내용");

        // request 설정
        UpdateScrapMemoDTO request = new UpdateScrapMemoDTO(scrap.getMemo());

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);
        when(scrapCommandService.updateScrapMemo(isA(MemberDTO.class), isA(Long.class), isA(UpdateScrapMemoDTO.class))).thenReturn(scrap);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/{scrap-id}/memo", scrap.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.scrapId").value(scrap.getId()))
                .andExpect(jsonPath("$.result.memo").value(request.getMemo()));
    }

    @DisplayName("[에러] 스크랩의 메모 수정 / request body: memo가 null")
    @Test
    public void errorScrapMemoModify_memoNull() throws Exception{
        //** given
        String token = setupStringToken();

        // 스크랩 설정
        Long scrapId = 999L;

        // request 설정
        UpdateScrapMemoDTO request = new UpdateScrapMemoDTO(null);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/{scrap-id}/memo", scrapId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("스크랩 삭제 (단건)")
    @Test
    public void scrapRemove() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member);
        ReflectionTestUtils.setField(category, "id", 98L);

        // 스크랩 설정
        Scrap scrap = setupScrap(member, category);

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/{scrap-id}/trash", scrap.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("스크랩 삭제 (목록)")
    @Test
    public void scrapListRemove() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category moveCategory = setupCategory(member);
        ReflectionTestUtils.setField(moveCategory, "id", 98L);

        // 스크랩 설정
        List<Scrap> scrapList = setupScrap(member, moveCategory, 5);

        // request 설정
        List<Long> scrapIdList = scrapList.stream()
                .map(scrap -> scrap.getId())
                .toList();
        DeleteScrapListDTO request = new DeleteScrapListDTO(scrapIdList);

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/trash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("[에러] 스크랩 삭제 (목록) / request body: scrapIdList가 빈 리스트")
    @Test
    public void errorScrapListRemove_scrapIdListEmpty() throws Exception{
        //** given
        String token = setupStringToken();

        // request 설정
        List<Long> scrapIdList = new ArrayList<>();
        DeleteScrapListDTO request = new DeleteScrapListDTO(scrapIdList);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/trash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 삭제 (목록) / request body: scrapIdList가 null")
    @Test
    public void errorScrapListRemove_scrapIdListNull() throws Exception{
        //** given
        String token = setupStringToken();

        // request 설정
        List<Long> scrapIdList = null;
        DeleteScrapListDTO request = new DeleteScrapListDTO(scrapIdList);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/auth/scraps/trash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    private String setupStringToken(){
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

    private Category setupCategory(Member member){
        return Category.builder()
                .member(member)
                .sequence(2)
                .isDefault(false)
                .title("수능")
                .build();
    }

    /**
     * 더미 스크랩 셋업하기
     * <br><br>
     * [기본] <br>
     * id = 999L <br>
     * isFavorite = false <br>
     * createdAt = 더미 스크랩이 생성된 시간
     */
    private Scrap setupScrap(Member member, Category category){
        Scrap scrap = Scrap.builder()
                .scrapURL("https://scrap")
                .title("마루는 강쥐")
                .memo("웹툰 너무 재밌다")
                .isFavorite(false)
                .description("네이버 웹툰")
                .imageURL("https://image")
                .member(member)
                .category(category)
                .build();
        ReflectionTestUtils.setField(scrap, "id", 999L);
        ReflectionTestUtils.setField(scrap, "createdAt", LocalDateTime.now());

        return scrap;
    }

    /**
     * 더미 스크랩 리스트 셋업하기
     * <br><br>
     * [기본] <br>
     * id = 999L ~ (999L-size) <br>
     * isFavorite = false <br>
     * createdAt = 더미 스크랩이 생성된 시간
     */
    private List<Scrap> setupScrap(Member member, Category category, int size){
        List<Scrap> scrapList = new ArrayList<>();

        for(int i=0; i<size; i++){
            Scrap scrap = Scrap.builder()
                    .scrapURL("https://scrap")
                    .title("마루는 강쥐")
                    .memo("웹툰 너무 재밌다")
                    .isFavorite(false)
                    .description("네이버 웹툰")
                    .imageURL("https://image")
                    .member(member)
                    .category(category)
                    .build();
            ReflectionTestUtils.setField(scrap, "id", 999L - i);
            ReflectionTestUtils.setField(scrap, "createdAt", LocalDateTime.now());

            scrapList.add(scrap);
        }

        return scrapList;
    }
}
