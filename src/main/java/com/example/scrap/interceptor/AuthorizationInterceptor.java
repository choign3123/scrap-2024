package com.example.scrap.interceptor;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.base.exception.ValidationException;
import com.example.scrap.entity.Member;
import com.example.scrap.jwt.ITokenProvider;
import com.example.scrap.jwt.dto.TokenType;
import com.example.scrap.redis.ILogoutBlacklistRedisUtils;
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

    private final ITokenProvider tokenProvider;
    private final IMemberQueryService memberQueryService;
    private final ILogoutBlacklistRedisUtils logoutBlacklistRedisUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){

        log.info("요청 URI: {}", request.getRequestURI());

        String accessToken = request.getHeader("Authorization");
        if(accessToken == null){
            throw new ValidationException("Authorization", "인증 토큰이 없습니다.");
        }

        // 로그아웃 처리된 토큰인지 검사
        if(logoutBlacklistRedisUtils.existToken(accessToken)){
            throw new AuthorizationException(ErrorCode.LOGOUT_TOKEN);
        }

        // 토큰 유효성 검사
        tokenProvider.isTokenValid(accessToken);

        // access 토큰이 맞는지 검사
        if(!tokenProvider.equalsTokenType(accessToken, TokenType.ACCESS)){
            throw new AuthorizationException(ErrorCode.NOT_ACCESS_TOKEN);
        }

        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(accessToken);
        Member member = memberQueryService.findMember(memberDTO);

        // member의 id와 snsId, snsType이 token의 값과 일치하는지 확인
        if(!memberDTO.isMatchMember(member)){
            throw new AuthorizationException(ErrorCode.TOKEN_VALUE_NOT_MATCH_TO_MEMBER);
        }

        log.info("auth 인터셉터-member: {}", member.getName());

        return true;
    }
}
