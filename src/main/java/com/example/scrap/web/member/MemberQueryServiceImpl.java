package com.example.scrap.web.member;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Member;
import com.example.scrap.jwt.TokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.jwt.dto.TokenType;
import com.example.scrap.web.member.dto.MemberRequest;
import com.example.scrap.web.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements IMemberQueryService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    /**
     * 멤버 조회
     */
    public Member findMember(MemberDTO memberDTO){
        return memberRepository.findById(memberDTO.getMemberId())
                .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 토큰 유효성 검사
     */
    public Token validateToken(MemberRequest.ValidateTokenDTO request){
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
}
