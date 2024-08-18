package com.example.scrap.member;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.MemberLog;
import com.example.scrap.entity.enums.LoginStatus;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.TokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.jwt.dto.TokenType;
import com.example.scrap.web.category.ICategoryCommandService;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.MemberCommandServiceImpl;
import com.example.scrap.web.member.MemberRepository;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.oauth.dto.NaverResponse;
import com.example.scrap.web.scrap.IScrapCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberCommandServiceImplTest {

    @InjectMocks
    private MemberCommandServiceImpl memberCommandService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private IMemberQueryService memberQueryService;

    @Mock
    private ICategoryCommandService categoryCommandService;

    @Mock
    private IScrapCommandService scrapCommandService;

    @Mock
    private TokenProvider tokenProvider;


    @DisplayName("네이버 회워가입")
    @Test
    public void signupNaver(){
        //** given
        // requestDTO 설정
        String naverId = "testNaverId";
        String name = "홍길동";
        NaverResponse.ProfileInfo.Response naverResponseDTO = new NaverResponse.ProfileInfo.Response(naverId, name);

        //** when
        Member newMember = memberCommandService.signup(naverResponseDTO);

        //** then
        assertThat(newMember.getSnsId())
                .isEqualTo(naverId);
        assertThat(newMember.getName())
                .isEqualTo(name);
        assertThat(newMember.getSnsType())
                .isEqualTo(SnsType.NAVER);
        assertThat(newMember.getMemberLog()) // MemberLog 설정됐는지 확인
                .isNotNull();
        verify(categoryCommandService).createDefaultCategory(newMember); // 기본 카테고리 생성 로직 실행됐는지 확인
    }

    @DisplayName("토큰 재발급")
    @Test
    public void reissueToken() {
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);
        MemberLog memberLog = new MemberLog();
        ReflectionTestUtils.setField(member, "memberLog", memberLog);

        // 갱신 토큰 설정
        String refreshToken = "testRefreshToken";

        // 재발급된 토큰 설정
        String newAccessToken = "newTestAccessToken";
        String newRefreshToken = "newTestRefreshToken";
        Token token = Token.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        when(tokenProvider.pasreRefreshToMemberDTO(refreshToken)).thenReturn(memberDTO);
        when(memberQueryService.findMemberWithLog(memberDTO)).thenReturn(member);
        when(tokenProvider.equalsTokenType(refreshToken, TokenType.REFRESH)).thenReturn(true);
        when(tokenProvider.reissueToken(refreshToken)).thenReturn(token);

        //** when
        Token reissueToken = memberCommandService.reissueToken(refreshToken);

        //** then
        assertThat(reissueToken.getAccessToken())
                .isEqualTo(newAccessToken);
        assertThat(reissueToken.getRefreshToken())
                .isEqualTo(newRefreshToken);

        verify(tokenProvider).isTokenValid(refreshToken); // 토큰 유효성 검사가 이뤄졌는지 확인
    }

    @DisplayName("[에러] 토큰 재발급 / 갱신 토큰이 아닌 경우")
    @Test
    public void errorReissueToken_notRefreshToken() {
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);
        MemberLog memberLog = new MemberLog();
        ReflectionTestUtils.setField(member, "memberLog", memberLog);

        // 갱신 토큰 설정
        String refreshToken = "testRefreshToken";

        when(tokenProvider.pasreRefreshToMemberDTO(refreshToken)).thenReturn(memberDTO);
        when(memberQueryService.findMemberWithLog(memberDTO)).thenReturn(member);
        when(tokenProvider.equalsTokenType(refreshToken, TokenType.REFRESH)).thenReturn(false);

        //** when
        Throwable throwable = catchThrowable(() -> {
            memberCommandService.reissueToken(refreshToken);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining(ErrorCode.NOT_REFRESH_TOKEN.getCode());
    }

    @DisplayName("[에러] 토큰 재발급 / 로그아웃 상태인 경우")
    @Test
    public void errorReissueToken_logoutStatus() {
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);
        MemberLog memberLog = new MemberLog();
        memberLog.logout(); // 로그아웃 상태로 만들기
        ReflectionTestUtils.setField(member, "memberLog", memberLog);

        // 갱신 토큰 설정
        String refreshToken = "testRefreshToken";

        when(tokenProvider.pasreRefreshToMemberDTO(refreshToken)).thenReturn(memberDTO);
        when(memberQueryService.findMemberWithLog(memberDTO)).thenReturn(member);
        when(tokenProvider.equalsTokenType(refreshToken, TokenType.REFRESH)).thenReturn(true);

        //** when
        Throwable throwable = catchThrowable(() -> {
            memberCommandService.reissueToken(refreshToken);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining(ErrorCode.LOGOUT_STATUS.getCode());
    }

    @DisplayName("로그아웃")
    public void logout(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);
        MemberLog memberLog = new MemberLog();
        ReflectionTestUtils.setField(member, "memberLog", memberLog);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);

        //** when
        memberCommandService.logout(memberDTO);

        //** then
        assertThat(member.getMemberLog().getLoginStatus())
                .isEqualTo(LoginStatus.LOGOUT);
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
