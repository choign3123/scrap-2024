package com.example.scrap.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthyCheckController {

    @GetMapping("/health")
    public String checkHealth(){
        return "I'm healthy! from 24.09.10";
    }
}
