package com.example.scrap.web.manageMember;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.jwt.TokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.manageMember.dto.ManageMemberRequest;
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

    /**
     * 토큰 유효성 검사
     */
    public Token validateToken(ManageMemberRequest.ValidateTokenDTO request){
        boolean isAccessValid, isRefreshValid;
        Token token;

        // accessToken과 refreshToken이 맞는지 확인
        if(!tokenProvider.isTokenTypeIsAccess(request.getAccessToken())){
            throw new AuthorizationException(ErrorCode.NOT_ACCESS_TOKEN);
        }
        if(!tokenProvider.isTokenTypeIsRefresh(request.getRefreshToken())){
            throw new AuthorizationException(ErrorCode.NOT_REFRESH_TOKEN);
        }

        // 토큰 만료 여부 확인
        isAccessValid = !tokenProvider.isTokenExpired(request.getAccessToken());
        isRefreshValid = !tokenProvider.isTokenExpired(request.getRefreshToken());

        token = Token.builder()
                .accessToken(request.getAccessToken())
                .refreshToken(request.getRefreshToken())
                .build();

        // access와 refresh의 멤버가 같은지 확인
        if(!tokenProvider.isMemberOfTokenSame(token)){
            throw new AuthorizationException(ErrorCode.ACCESS_MEMBER_AND_REFRESH_MEMBER_NOT_MATCH);
        }

        if(isAccessValid == false && isRefreshValid == false){ // access[X] refresh[X]
            throw new AuthorizationException(ErrorCode.TOKEN_EXPIRED);
        }
        else if(isAccessValid == true && isRefreshValid == true){ // access[O] refresh[O]
            if(tokenProvider.isRequiredTokenReissue(token.getAccessToken())){ // accessToken 갱신이 필요한 경우
                token = tokenProvider.reissueAccessToken(token);
            }
            if(tokenProvider.isRequiredTokenReissue(token.getRefreshToken())){ // refresh 갱신이 필요한 경우
                token = tokenProvider.reissueRefreshToken(token);
            }
        }
        else if(isAccessValid == false && isRefreshValid == true){ // access[X] refresh[O]
            token = tokenProvider.reissueAccessToken(token); // accessToken 갱신

            if(tokenProvider.isRequiredTokenReissue(token.getRefreshToken())){ // refresh 갱신이 필요한 경우
                token = tokenProvider.reissueRefreshToken(token);
            }
        }
        else if(isAccessValid == true && isRefreshValid == false){ // access[O] refresh[X]
            if(tokenProvider.isRequiredTokenReissue(token.getAccessToken())){ // accessToken 갱신이 필요한 경우
                token = tokenProvider.reissueAccessToken(token);
            }
        }

        return token;
    }
}
