package com.example.scrap.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Tag(name = "Healthy Check", description = "Health Check 관련 API")
public class HealthyCheckController {

    @Operation(
            summary = "healthy check"
    )
    @GetMapping("/health")
    public String checkHealth(){
        return "I'm healthy! " + LocalDateTime.now();
    }
}
