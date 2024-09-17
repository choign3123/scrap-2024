package com.example.scrap.mypage;

import com.example.scrap.base.exception.GlobalExceptionHandler;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.ITokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.mypage.IMypageQueryService;
import com.example.scrap.web.mypage.MypageController;
import com.example.scrap.web.mypage.dto.MypageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MypageControllerTest {

    @InjectMocks
    private MypageController mypageController;

    @Mock
    private IMypageQueryService mypageService;

    @Mock
    private ITokenProvider tokenProvider;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(mypageController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
    }

    @DisplayName("마이페이지 조회")
    @Test
    public void mypage() throws Exception {
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        // response 설정
        MypageResponse.MypageDTO response = MypageResponse.MypageDTO.builder()
                .name("최가나")
                .totalScrap(123L)
                .totalCategory(7L)
                .build();

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);
        when(mypageService.mypage(memberDTO)).thenReturn(response);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/mypage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.memberInfo.name").value(response.getMemberInfo().getName()))
                .andExpect(jsonPath("$.result.statistics.totalCategory").value(response.getStatistics().getTotalCategory()))
                .andExpect(jsonPath("$.result.statistics.totalScrap").value(response.getStatistics().getTotalScrap()));
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

}
