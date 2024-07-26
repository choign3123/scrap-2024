package com.example.scrap.web.member;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.converter.MemberConverter;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.MemberLog;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.TokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.category.ICategoryCommandService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.oauth.dto.NaverResponse;
import com.example.scrap.web.scrap.IScrapCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements IMemberCommandService {

    private final MemberRepository memberRepository;
    private final IMemberQueryService memberQueryService;
    private final ICategoryCommandService categoryCommandService;
    private final IScrapCommandService scrapCommandService;
    private final TokenProvider tokenProvider;

    /**
     * 네이버 회원가입
     */
    public Member signup(NaverResponse.ProfileInfo.Response profileInfo){
        MemberLog memberLog = new MemberLog();
        Member member = MemberConverter.toEntity(profileInfo, SnsType.NAVER, memberLog);

        // 기본 카테고리 생성
        categoryCommandService.createDefaultCategory(member);

        return memberRepository.save(member);
    }

    /**
     * 토큰 재발급
     */
    public Token reissueToken(String refreshToken){

        refreshToken = tokenProvider.removeTokenPrefix(refreshToken);

        // 토큰 유효성 검사
        tokenProvider.isTokenValid(refreshToken);

        MemberDTO memberDTO = tokenProvider.pasreRefreshToMemberDTO(refreshToken);
        Member member = memberQueryService.findMember(memberDTO);

        // refresh 토큰이 맞는지 검사
        if(!tokenProvider.isTokenTypeIsRefresh(refreshToken)){
            throw new AuthorizationException(ErrorCode.NOT_REFRESH_TOKEN);
        }

        // 로그인한 유저인지 검사
        switch (member.getMemberLog().getLoginStatus()){
            case ACTIVE -> {} // pass
            case LOGOUT -> throw new AuthorizationException(ErrorCode.LOGOUT_STATUS);
        }

        // 토큰 재발급
        return tokenProvider.reissueToken(refreshToken);
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(MemberDTO memberDTO){
        Member member = memberQueryService.findMember(memberDTO);

        member.logout();
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
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
