package com.example.scrap;

import com.example.scrap.base.data.DefaultData;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;


@SpringBootApplication
@EnableJpaAuditing
@OpenAPIDefinition(
		servers = @Server(url = "/", description = "Generated Server URL")
)
public class ScrapApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrapApplication.class, args);
		System.setProperty(DefaultData.DEPLOY_TIME_KEY, LocalDateTime.now().toString());
	}

}
