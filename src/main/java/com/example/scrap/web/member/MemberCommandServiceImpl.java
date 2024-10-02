package com.example.scrap.web.member;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.converter.MemberConverter;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.MemberLog;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.ITokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.jwt.dto.TokenType;
import com.example.scrap.redis.ILogoutBlacklistRedisUtils;
import com.example.scrap.web.category.ICategoryCommandService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.oauth.IOauthMemberInfoProvider;
import com.example.scrap.web.oauth.OauthMemberInfoFactory;
import com.example.scrap.web.oauth.dto.CommonOauthMemberInfo;
import com.example.scrap.web.scrap.IScrapCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements IMemberCommandService {

    private final MemberRepository memberRepository;
    private final IMemberQueryService memberQueryService;
    private final ICategoryCommandService categoryCommandService;
    private final IScrapCommandService scrapCommandService;
    private final ITokenProvider tokenProvider;
    private final ILogoutBlacklistRedisUtils logoutBlacklistRedisUtils;
    private final OauthMemberInfoFactory oauthMemberInfoFactory;

    /**
     * 로그인/회원가입 (통합)
     * @return 회원가입이 되어있지 않은 회원의 경우, 자동 회원가입 후 token 반환
     */
    public Token integrationLoginSignup(String authorization, SnsType snsType){

        // 회원 정보 조회하기
        IOauthMemberInfoProvider oauthMemberInfoProvider = oauthMemberInfoFactory.getOauthMemberInfoProvider(snsType);
        CommonOauthMemberInfo memberInfo = oauthMemberInfoProvider.getMemberId(authorization);

        Optional<Member> optionalMember = memberRepository.findBySnsTypeAndSnsId(snsType, memberInfo.getSnsId());

        // db에 없으면 해당 정보로 로그인 후, 토큰 생성해서 return
        // db에 있으면 해당 정보로 토큰 생성해서 return
        Member member = optionalMember.orElseGet(
                () -> signup(memberInfo, snsType)
        );

        member.login();

        return tokenProvider.createToken(member);
    }

    /**
     * 회원가입
     */
    private Member signup(CommonOauthMemberInfo memberInfo, SnsType snsType){
        MemberLog memberLog = new MemberLog();
        Member member = MemberConverter.toEntity(memberInfo, snsType, memberLog);

        // 기본 카테고리 생성
        categoryCommandService.createDefaultCategory(member);

        return memberRepository.save(member);
    }

    /**
     * 토큰 재발급
     * @throws AuthorizationException refresh 토큰이 아닐 경우
     * @throws AuthorizationException 로그아웃 상태일 경우
     * @throws AuthorizationException 토큰이 만료되었을 경우
     */
    public Token reissueToken(String refreshToken){

        // 토큰 유효성 검사
        tokenProvider.isTokenValid(refreshToken);

        // refresh 토큰인지 확인
        if(!tokenProvider.equalsTokenType(refreshToken, TokenType.REFRESH)) {
            throw new AuthorizationException(ErrorCode.NOT_REFRESH_TOKEN);
        }

        Member member = memberQueryService.findMember(tokenProvider.parseRefreshToMemberDTO(refreshToken));

        // 토큰 재발급
        return tokenProvider.reissueToken(refreshToken, member);
    }

    /**
     * 로그아웃
     */
    public void logout(MemberDTO memberDTO, String token){
        Member member = memberQueryService.findMember(memberDTO);

        logoutBlacklistRedisUtils.addLogoutToken(token, member);

        member.logout();
    }

    /**
     * 회원 탈퇴
     */
    public void signOut(MemberDTO memberDTO){
        Member member = memberQueryService.findMember(memberDTO);

        // 스크랩 전체 삭제
        scrapCommandService.deleteAllScrap(memberDTO);

        // 카테고리 전체 삭제
        categoryCommandService.deleteAllCategory(memberDTO);

        // 회원 삭제
        memberRepository.delete(member);
    }
}
