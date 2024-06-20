package com.example.scrap.interceptor;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.entity.Member;
import com.example.scrap.jwt.TokenProvider;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final TokenProvider tokenProvider;
    private final IMemberQueryService memberQueryService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String accessToken = request.getHeader("Authorization");
        MemberDTO memberDTO = tokenProvider.parseMemberDTO(accessToken);
        Member member = memberQueryService.findMember(memberDTO);

        if(tokenProvider.isTokenExpired(accessToken)){ // 토큰 만료
            throw new AuthorizationException(ErrorCode.TOKEN_EXPIRED);
        }
        if(!tokenProvider.isTokenTypeIsAccess(accessToken)){ // access 토큰이 아님
            throw new AuthorizationException(ErrorCode.NOT_ACCESS_TOKEN);
        }
        if(!memberDTO.isMatchMember(member)){  // member의 id와 snsId, snsType이 token의 값과 일치하는지 확인
            throw new AuthorizationException(ErrorCode.MEMBER_NOT_MATCH_TO_MEMBER_DTO);
        }

        log.info("auth 인터셉터-member: {}", member.getName());

        return true;
    }
}
