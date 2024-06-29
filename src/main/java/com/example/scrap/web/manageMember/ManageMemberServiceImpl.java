package com.example.scrap.web.manageMember;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.entity.Member;
import com.example.scrap.jwt.TokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.jwt.dto.TokenType;
import com.example.scrap.web.category.ICategoryCommandService;
import com.example.scrap.web.manageMember.dto.ManageMemberRequest;
import com.example.scrap.web.member.IMemberCommandService;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.scrap.IScrapCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ManageMemberServiceImpl implements IMangeMemberService{

    private final TokenProvider tokenProvider;
    private final IMemberQueryService memberQueryService;
    private final IMemberCommandService memberCommandService;
    private final ICategoryCommandService categoryCommandService;
    private final IScrapCommandService scrapCommandService;

    /**
     * 토큰 유효성 검사
     */
    public Token validateToken(ManageMemberRequest.ValidateTokenDTO request){
        return validateToken(request.getAccessToken(), request.getRefreshToken());
    }

    /**
     * 토큰 유효성 검사
     */
    public Token validateToken(String accessToken, String refreshToken){
        boolean isAccessValid, isRefreshValid;
        Token token;

        // 토큰 만료 여부 확인
        isAccessValid = tokenProvider.isTokenValid(accessToken);
        isRefreshValid = tokenProvider.isTokenValid(refreshToken);

        token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        // 토큰이 유효할 때 공통적으로 처리해야 되는 작업 수행
        if(isAccessValid){
            // access 토큰이 맞는지 확인
            if(!tokenProvider.isTokenTypeIsAccess(token.getAccessToken())){
                throw new AuthorizationException(ErrorCode.NOT_ACCESS_TOKEN);
            }

            // accessToken 갱신이 필요한 경우
            if(tokenProvider.isRequiredTokenReissue(token.getAccessToken())){
                token = tokenProvider.reissueAccessToken(token, TokenType.ACCESS);
            }
        }
        if(isRefreshValid){
            // refresh 토큰이 맞는지 확인
            if(!tokenProvider.isTokenTypeIsRefresh(token.getRefreshToken())){
                throw new AuthorizationException(ErrorCode.NOT_REFRESH_TOKEN);
            }
        }

        // 각 상황에 대해 특별히 필요한 작업 수행
        if(isAccessValid == false && isRefreshValid == false){ // access[X] refresh[X]
            throw new AuthorizationException(ErrorCode.TOKEN_NOT_VALID);
        }
        else if(isAccessValid == true && isRefreshValid == true){ // access[O] refresh[O]
            // access와 refresh의 멤버가 같은지 확인
            if(!tokenProvider.isMemberOfTokenSame(token)){
                throw new AuthorizationException(ErrorCode.ACCESS_MEMBER_AND_REFRESH_MEMBER_NOT_MATCH);
            }

            // refresh 갱신이 필요한 경우
            if(tokenProvider.isRequiredTokenReissue(token.getRefreshToken())){
                token = tokenProvider.reissueRefreshToken(token);
            }
        }
        else if(isAccessValid == false && isRefreshValid == true){ // access[X] refresh[O]
            token = tokenProvider.reissueAccessToken(token, TokenType.REFRESH); // accessToken 갱신
        }
        else if(isAccessValid == true && isRefreshValid == false){ // access[O] refresh[X]
            // 특별히 필요한 작업 없음
        }

        return token;
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
        memberCommandService.signOut(member);
    }
}
