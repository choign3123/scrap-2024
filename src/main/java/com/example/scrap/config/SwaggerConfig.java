package com.example.scrap.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .title("스크랩-2024")
                .version("1.0")
                .description("스크랩-2024 API 문서");

        /* JWT 인증 헤더 설정 **/
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        Components components = new Components()
                .addSecuritySchemes("bearer-key", securityScheme);
        /* (끝) JWT 인증 헤더 설정 **/

        return new OpenAPI()
                .components(components)
                .info(info);
    }

}
