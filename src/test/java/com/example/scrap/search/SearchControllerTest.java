package com.example.scrap.search;


import com.example.scrap.base.enums.SearchScopeType;
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
import com.example.scrap.web.search.ISearchQueryService;
import com.example.scrap.web.search.SearchController;
import com.example.scrap.web.search.dto.SearchRequest.*;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
public class SearchControllerTest {

    @MockBean
    private ISearchQueryService searchService;

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


    @DisplayName("스크랩 검색하기")
    @Test
    public void scrapSearch() throws Exception{
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // 카테고리 설정
        Category category = setupCategory(member);

        // 스크랩 설정
        List<Scrap> scrapList = setupScrap(member, category, 5);
        Page<Scrap> scrapPage = new PageImpl<>(scrapList);

        // request 설정
        FindScrapDTOTemp request = new FindScrapDTOTemp();
        request.setSearchScope(List.of(SearchScopeType.MEMO.name()));
        request.setCategoryScope(List.of(99L, 98L));
        request.setStartDate("2024-09-01");
        request.setEndDate("2024-09-17");

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 10;
        String q = "마루";

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);
        when(searchService.findScrap(eq(memberDTO), isA(FindScrapDTO.class), isA(PageRequest.class), isA(String.class)))
                .thenReturn(scrapPage);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.name())
                        .param("direction", direction.name())
                        .param("page", page.toString())
                        .param("size", size.toString())
                        .param("q", q)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.meta.numOfElement").value(scrapList.size()));
    }

    @DisplayName("[에러] 스크랩 검색하기 / request body: endDate가 startDate보다 앞섬")
    @Test
    public void errorScrapSearch_startDateNull() throws Exception{
        //** given
        String token = setupStringToken();

        // request 설정
        FindScrapDTOTemp request = new FindScrapDTOTemp();
        request.setSearchScope(List.of(SearchScopeType.MEMO.name()));
        request.setCategoryScope(List.of(99L, 98L));
        request.setStartDate("2024-09-30");
        request.setEndDate("2024-09-01");

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 10;
        String q = "마루";

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.name())
                        .param("direction", direction.name())
                        .param("page", page.toString())
                        .param("size", size.toString())
                        .param("q", q)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 검색하기 / query string: 잘못된 sort값")
    @Test
    public void errorScrapSearch_sortWrongValue() throws Exception{
        //** given
        String token = setupStringToken();

        // request 설정
        FindScrapDTOTemp request = new FindScrapDTOTemp();
        request.setSearchScope(List.of(SearchScopeType.MEMO.name()));
        request.setCategoryScope(List.of(99L, 98L));
        request.setStartDate("2024-09-30");
        request.setEndDate("2024-09-01");

        // query string 설정
//        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 10;
        String q = "마루";

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", "이상한 값")
                        .param("direction", direction.name())
                        .param("page", page.toString())
                        .param("size", size.toString())
                        .param("q", q)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 검색하기 / query string: 잘못된 direction값")
    @Test
    public void errorScrapSearch_directionWrongValue() throws Exception{
        //** given
        String token = setupStringToken();

        // request 설정
        FindScrapDTOTemp request = new FindScrapDTOTemp();
        request.setSearchScope(List.of(SearchScopeType.MEMO.name()));
        request.setCategoryScope(List.of(99L, 98L));
        request.setStartDate("2024-09-30");
        request.setEndDate("2024-09-01");

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
//        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 10;
        String q = "마루";

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.getName())
                        .param("direction", "이상한 값")
                        .param("page", page.toString())
                        .param("size", size.toString())
                        .param("q", q)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 검색하기 / query string: page가 0 미만")
    @Test
    public void errorScrapSearch_pageUnder0() throws Exception{
        //** given
        String token = setupStringToken();

        // request 설정
        FindScrapDTOTemp request = new FindScrapDTOTemp();
        request.setSearchScope(List.of(SearchScopeType.MEMO.name()));
        request.setCategoryScope(List.of(99L, 98L));
        request.setStartDate("2024-09-30");
        request.setEndDate("2024-09-01");

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = -1;
        Integer size = 10;
        String q = "마루";

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.getName())
                        .param("direction", direction.name())
                        .param("page", page.toString())
                        .param("size", size.toString())
                        .param("q", q)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 검색하기 / query string: size가 1 미만")
    @Test
    public void errorScrapSearch_sizeUnder1() throws Exception{
        //** given
        String token = setupStringToken();

        // request 설정
        FindScrapDTOTemp request = new FindScrapDTOTemp();
        request.setSearchScope(List.of(SearchScopeType.MEMO.name()));
        request.setCategoryScope(List.of(99L, 98L));
        request.setStartDate("2024-09-30");
        request.setEndDate("2024-09-01");

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 0;
        String q = "마루";

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.getName())
                        .param("direction", direction.name())
                        .param("page", page.toString())
                        .param("size", size.toString())
                        .param("q", q)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 검색하기 / query string: size가 30 초과")
    @Test
    public void errorScrapSearch_sizeUp30() throws Exception{
        //** given
        String token = setupStringToken();

        // request 설정
        FindScrapDTOTemp request = new FindScrapDTOTemp();
        request.setSearchScope(List.of(SearchScopeType.MEMO.name()));
        request.setCategoryScope(List.of(99L, 98L));
        request.setStartDate("2024-09-30");
        request.setEndDate("2024-09-01");

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 31;
        String q = "마루";

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.getName())
                        .param("direction", direction.name())
                        .param("page", page.toString())
                        .param("size", size.toString())
                        .param("q", q)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("[에러] 스크랩 검색하기 / query string: q가 빈 문자열")
    @Test
    public void errorScrapSearch_qBlank() throws Exception{
        //** given
        String token = setupStringToken();

        // request 설정
        FindScrapDTOTemp request = new FindScrapDTOTemp();
        request.setSearchScope(List.of(SearchScopeType.MEMO.name()));
        request.setCategoryScope(List.of(99L, 98L));
        request.setStartDate("2024-09-30");
        request.setEndDate("2024-09-01");

        // query string 설정
        Sorts sorts = Sorts.SCRAP_DATE;
        Sort.Direction direction = Sort.Direction.DESC;
        Integer page = 0;
        Integer size = 10;
        String q = "";

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("sort", sorts.getName())
                        .param("direction", direction.name())
                        .param("page", page.toString())
                        .param("size", size.toString())
                        .param("q", q)
                        .content(new Gson().toJson(request))
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    // Gson이 LocalDate를 변환하지 못해서 임시로 사용할 DTO 추가
    private class FindScrapDTOTemp{
        List<String> searchScope;
        List<Long> categoryScope;
        String startDate;
        String endDate;

        public List<String> getSearchScope() {
            return searchScope;
        }

        public void setSearchScope(List<String> searchScope) {
            this.searchScope = searchScope;
        }

        public List<Long> getCategoryScope() {
            return categoryScope;
        }

        public void setCategoryScope(List<Long> categoryScope) {
            this.categoryScope = categoryScope;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
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
