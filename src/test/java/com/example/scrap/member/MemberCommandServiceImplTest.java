package com.example.scrap.member;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.MemberLog;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.ITokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.jwt.dto.TokenType;
import com.example.scrap.redis.LogoutBlacklistRedisUtils;
import com.example.scrap.web.category.ICategoryCommandService;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.MemberCommandServiceImpl;
import com.example.scrap.web.member.MemberRepository;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.oauth.NaverMemberIntoProvider;
import com.example.scrap.web.oauth.OauthMemberInfoFactory;
import com.example.scrap.web.oauth.dto.CommonOauthMemberInfo;
import com.example.scrap.web.scrap.IScrapCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
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
    private ITokenProvider tokenProvider;

    @Mock
    private OauthMemberInfoFactory oauthMemberInfoFactory;

    @Mock
    private NaverMemberIntoProvider naverMemberIntoProvider;

    @Mock
    private LogoutBlacklistRedisUtils logoutBlacklistRedisUtils;

    @DisplayName("로그인 (회원가입) - 로그인")
    @Test
    public void integrationLoginSignup_login(){
        //** given
        Member member = setupMember();
        MemberLog memberLog = new MemberLog();
        ReflectionTestUtils.setField(member, "memberLog", memberLog);
        String authorization = "tempAuthorizationValue";
        SnsType snsType = SnsType.NAVER;
        CommonOauthMemberInfo oauthMemberInfo = CommonOauthMemberInfo.builder()
                .name(member.getName())
                .snsId(member.getSnsId())
                .build();

        // 토큰 설정
        Token token = Token.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        when(oauthMemberInfoFactory.getOauthMemberInfoProvider(snsType)).thenReturn(naverMemberIntoProvider);
        when(naverMemberIntoProvider.getMemberId(authorization)).thenReturn(oauthMemberInfo);
        when(memberRepository.findBySnsTypeAndSnsId(snsType, oauthMemberInfo.getSnsId())).thenReturn(Optional.of(member));
        when(tokenProvider.createToken(member)).thenReturn(token);

        //** when
        Token newToken = memberCommandService.integrationLoginSignup(authorization, snsType);

        //** then
        assertThat(newToken.getAccessToken())
                .isEqualTo(token.getAccessToken());
        assertThat(newToken.getRefreshToken())
                .isEqualTo(token.getRefreshToken());
    }

    @DisplayName("로그인 (회원가입) - 회원가입")
    @Test
    public void integrationLoginSignup_signup(){
        //** given
        Member member = setupMember();
        MemberLog memberLog = new MemberLog();
        ReflectionTestUtils.setField(member, "memberLog", memberLog);
        String authorization = "tempAuthorizationValue";
        SnsType snsType = SnsType.NAVER;
        CommonOauthMemberInfo oauthMemberInfo = CommonOauthMemberInfo.builder()
                .name(member.getName())
                .snsId(member.getSnsId())
                .build();

        // 토큰 설정
        Token token = Token.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        when(oauthMemberInfoFactory.getOauthMemberInfoProvider(snsType)).thenReturn(naverMemberIntoProvider);
        when(naverMemberIntoProvider.getMemberId(authorization)).thenReturn(oauthMemberInfo);
        when(memberRepository.findBySnsTypeAndSnsId(snsType, oauthMemberInfo.getSnsId())).thenReturn(Optional.empty());
        when(memberRepository.save(isA(Member.class))).thenReturn(member);
        when(tokenProvider.createToken(member)).thenReturn(token);

        //** when
        Token newToken = memberCommandService.integrationLoginSignup(authorization, snsType);

        //** then
        assertThat(newToken.getAccessToken())
                .isEqualTo(token.getAccessToken());
        assertThat(newToken.getRefreshToken())
                .isEqualTo(token.getRefreshToken());

        verify(categoryCommandService).createDefaultCategory(isA(Member.class));
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

        when(tokenProvider.equalsTokenType(refreshToken, TokenType.REFRESH)).thenReturn(true);
        when(tokenProvider.reissueToken(refreshToken, member)).thenReturn(token);
        when(tokenProvider.parseRefreshToMemberDTO(refreshToken)).thenReturn(memberDTO);
        when(memberQueryService.findMember(memberDTO)).thenReturn(member);

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

    @DisplayName("로그아웃")
    @Test
    public void logout(){
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);
        MemberLog memberLog = new MemberLog();
        ReflectionTestUtils.setField(member, "memberLog", memberLog);

        // 임의 토큰 설정
        String token = "tempToken";

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);

        //** when
        memberCommandService.logout(memberDTO, token);

        //** then
        assertThat(member.getMemberLog().getRefreshTokenId())
                .isEqualTo(0L);
        verify(logoutBlacklistRedisUtils).addLogoutToken(token, member);
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
