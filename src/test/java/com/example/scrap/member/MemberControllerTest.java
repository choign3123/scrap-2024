package com.example.scrap.member;

import com.example.scrap.base.exception.GlobalExceptionHandler;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.ITokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.member.IMemberCommandService;
import com.example.scrap.web.member.MemberController;
import com.example.scrap.web.member.dto.MemberDTO;
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
public class MemberControllerTest {

    @InjectMocks
    private MemberController memberController;

    @Mock
    private IMemberCommandService memberCommandService;

    @Mock
    private ITokenProvider tokenProvider;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
    }

    @DisplayName("로그인/회원가입")
    @Test
    public void integrationLoginSignup() throws Exception{
        //** given
        String authorization = "tempAuthorizationValue";
        Token token = setupToken();

        // query string
        SnsType snsType = SnsType.NAVER;

        when(memberCommandService.integrationLoginSignup(isA(String.class), isA(SnsType.class))).thenReturn(token);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/oauth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorization)
                        .param("sns", snsType.name())
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.accessToken").value(token.getAccessToken()))
                .andExpect(jsonPath("$.result.refreshToken").value(token.getRefreshToken()));
    }

    @DisplayName("토큰 유효성 검사")
    @Test
    public void tokenValidate() throws Exception {
        //** given
        String token = setupStringToken();

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/token/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("토큰 재발급")
    @Test
    public void tokenReissue() throws Exception {
        //** given
        String refreshToken = setupStringToken();
        Token token = setupToken();

        when(memberCommandService.reissueToken(isA(String.class))).thenReturn(token);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("refresh_token", refreshToken)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.accessToken").value(token.getAccessToken()))
                .andExpect(jsonPath("$.result.refreshToken").value(token.getRefreshToken()));
    }

    @DisplayName("로그아웃")
    @Test
    public void logout() throws Exception {
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);


        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("회원탈퇴")
    @Test
    public void signOut() throws Exception {
        //** given
        String token = setupStringToken();
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        when(tokenProvider.parseAccessToMemberDTO(isA(String.class))).thenReturn(memberDTO);

        //** when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/signout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
        );

        //** then
        resultActions
                .andDo(print())
                .andExpect(status().isOk());
    }

    private String setupStringToken(){
        return "memberId:1,snsId:gana,snsType:NAVER";
    }

    private Token setupToken(){
        return Token.builder()
                .accessToken("accessMemberId:1,snsId:gana,snsType:NAVER")
                .refreshToken("refreshMemberId:1,snsId:gana,snsType:NAVER")
                .build();
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
