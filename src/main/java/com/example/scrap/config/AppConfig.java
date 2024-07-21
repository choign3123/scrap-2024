package com.example.scrap.config;

import com.example.scrap.interceptor.AuthorizationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

        // accessToken 인증 인터셉터 등록
        registry.addInterceptor(authorizationInterceptor)
                .order(2) // OpenEntityManagerInViewInterceptor 를 먼저 동작시키게 하기 위해서 우선순위 낮춤.
                .addPathPatterns("/**")
                .excludePathPatterns("/oauth/login/**")
                .excludePathPatterns("/me")
                .excludePathPatterns("/oauth/naver/callback")
                .excludePathPatterns("/token");
    }
}
