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

/**
 * 로그인 상태에서만 사용 가능한 API를 걸러내기 위함.
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class LoginStatusInterceptor implements HandlerInterceptor {

    private final TokenProvider tokenProvider;
    private final IMemberQueryService memberQueryService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = request.getHeader("Authorization");

        MemberDTO memberDTO = tokenProvider.parseMemberDTO(accessToken);
        Member member = memberQueryService.findMember(memberDTO);

        log.info("login 인터셉터-member: {}", member.getName());

        // 로그인 상태인지 확인
        switch (member.getMemberLog().getLoginStatus()){
            case ACTIVE -> {return true;}
            case LOGOUT -> throw new AuthorizationException(ErrorCode.LOGOUT_STATUS);
            case UNREGISTER -> throw new AuthorizationException(ErrorCode.UNREGISTER_STATUS);
            default -> throw new AuthorizationException(ErrorCode.MEMBER_LOG_STATUS_NOT_MATCH);
        }
    }
}
