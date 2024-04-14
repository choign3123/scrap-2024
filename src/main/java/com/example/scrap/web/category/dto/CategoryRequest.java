package com.example.scrap.web.category.dto;

import lombok.Getter;

public class CategoryRequest {

    @Getter
    public static class CreateCategoryDTO{
        private String categoryTitle;
    }
}
