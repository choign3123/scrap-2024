package com.example.scrap.config;

import com.example.scrap.interceptor.AuthorizationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AppConfig implements WebMvcConfigurer {

    private final AuthorizationInterceptor authorizationInterceptor;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 소셜 로그인 관련은 토큰 인증 제외
        List<String> excludeSocialPatternList = new ArrayList<>();
        excludeSocialPatternList.add("/oauth/naver/callback");
        excludeSocialPatternList.add("/oauth/kakao/callback");

        // 스웨거 관련은 토큰 인증 제외
        List<String> excludeSwaggerPatternList = new ArrayList<>();
        excludeSwaggerPatternList.add("/v3/api-docs/**");
        excludeSwaggerPatternList.add("/swagger-ui/**");
        excludeSwaggerPatternList.add("/swagger");


        // accessToken 인증 인터셉터 등록
        registry.addInterceptor(authorizationInterceptor)
                .order(2) // OpenEntityManagerInViewInterceptor 를 먼저 동작시키게 하기 위해서 우선순위 낮춤.
                .addPathPatterns("/**")
                .excludePathPatterns("/oauth/login/**")
                .excludePathPatterns("/health")
                .excludePathPatterns("/token") // 토큰 재발급
                .excludePathPatterns(excludeSocialPatternList)
                .excludePathPatterns(excludeSwaggerPatternList);
    }
}
