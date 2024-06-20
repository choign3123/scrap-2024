package com.example.scrap.config;

import com.example.scrap.interceptor.AuthorizationInterceptor;
import com.example.scrap.interceptor.LoginAndLogoutStatusInterceptor;
import com.example.scrap.interceptor.LoginStatusInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AppConfig implements WebMvcConfigurer {

    private final AuthorizationInterceptor authorizationInterceptor;
    private final LoginStatusInterceptor loginStatusInterceptor;
    private final LoginAndLogoutStatusInterceptor loginAndLogoutStatusInterceptor;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // accessToken 인증 인터셉터 등록
        registry.addInterceptor(authorizationInterceptor)
                .order(2) // OpenEntityManagerInViewInterceptor 를 먼저 동작시키게 하기 위해서 우선순위 낮춤.
                .addPathPatterns("/**")
                .excludePathPatterns("/oauth/login/**")
                .excludePathPatterns("/me");

        // 로그인 상태에서만 사용가능한 API
        registry.addInterceptor(loginStatusInterceptor)
                .order(3) // OpenEntityManagerInViewInterceptor 를 먼저 동작시키게 하기 위해서 우선순위 낮춤.
                .addPathPatterns("/**")
                .excludePathPatterns("/oauth/login/**")
                .excludePathPatterns("/me");

        // 로그아웃 상태에서 사용 가능한 API
        registry.addInterceptor(loginAndLogoutStatusInterceptor)
                .order(3) // OpenEntityManagerInViewInterceptor 를 먼저 동작시키게 하기 위해서 우선순위 낮춤.
                .addPathPatterns("/me")
                .excludePathPatterns("/oauth/login/**");

        // [TODO] 로그인, 토큰 유효성 조회 API는 빼기
    }
}
