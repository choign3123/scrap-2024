package com.example.scrap.interceptor;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.base.exception.ValidationException;
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
        if(accessToken == null){
            throw new ValidationException("Authorization", "인증 토큰이 없습니다.");
        }

        if(!tokenProvider.isTokenValid(accessToken)){ // 토큰 유효성 검사
            throw new AuthorizationException(ErrorCode.TOKEN_NOT_VALID);
        }
        if(!tokenProvider.isTokenTypeIsAccess(accessToken)){ // access 토큰이 아님
            throw new AuthorizationException(ErrorCode.NOT_ACCESS_TOKEN);
        }

        // member의 id와 snsId, snsType이 token의 값과 일치하는지 확인
        MemberDTO memberDTO = tokenProvider.parseMemberDTO(accessToken);
        Member member = memberQueryService.findMember(memberDTO);
        if(!memberDTO.isMatchMember(member)){
            throw new AuthorizationException(ErrorCode.TOKEN_VALUE_NOT_MATCH_TO_MEMBER);
        }

        log.info("auth 인터셉터-member: {}", member.getName());

        return true;
    }
}
