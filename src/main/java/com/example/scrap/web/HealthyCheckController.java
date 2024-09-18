package com.example.scrap.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Healthy Check", description = "Health Check 관련 API")
public class HealthyCheckController {

    @GetMapping("/health")
    public String checkHealth(){
        return "I'm healthy! from 24.09.10";
    }
}
