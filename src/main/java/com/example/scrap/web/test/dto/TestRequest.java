package com.example.scrap.web.test.dto;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
public class TestRequest {

    @NotBlank
    private String name;

    @Min(0)
    private int age;
}
