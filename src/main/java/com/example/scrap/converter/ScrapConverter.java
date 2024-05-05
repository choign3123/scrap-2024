package com.example.scrap.converter;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.web.scrap.dto.ScrapRequest;
import com.example.scrap.web.scrap.dto.ScrapResponse;

public class ScrapConverter {

    public static Scrap toEntity(ScrapRequest.CreateScrap request, Member member, Category category){
        return Scrap.builder()
                .scrapURL(request.getScrapURL())
                .imageURL(request.getImageURL())
                .title(request.getTitle())
                .description(request.getDescription())
                .memo(request.getMemo())
                .member(member)
                .category(category)
                .build();
    }

    public static ScrapResponse.CreateScrapDTO toCreateScrapDTO(Scrap scrap){
        return ScrapResponse.CreateScrapDTO.builder()
                .scrapId(scrap.getId())
                .title(scrap.getTitle())
                .scrapURL(scrap.getScrapURL())
                .imageURL(scrap.getImageURL())
                .isStar(scrap.getStar())
                .scrapDate(scrap.getCreatedAt().toLocalDate())
                .build();
    }
}
