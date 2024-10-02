package com.example.scrap.web;

import com.example.scrap.base.data.DefaultData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Healthy Check", description = "Health Check 관련 API")
public class HealthyCheckController {

    @Operation(
            summary = "healthy check"
    )
    @GetMapping("/health")
    public String checkHealth(){
        return "I'm healthy! " + System.getProperty(DefaultData.DEPLOY_TIME_KEY);
    }
}
