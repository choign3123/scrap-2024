package com.example.scrap.converter;

import com.example.scrap.entity.Scrap;
import com.example.scrap.web.baseDTO.Meta;
import com.example.scrap.web.search.dto.SearchResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class SearchConverter {

    public static SearchResponse.FindScrapDTO toFindScrapDTO(Page<Scrap> scrapPage){

        List<SearchResponse.FindScrapDTO.ScrapDTO> scrapDTOList = scrapPage.stream()
                .map(scrap -> {
                    return SearchResponse.FindScrapDTO.ScrapDTO.builder()
                            .scrapId(scrap.getId())
                            .categoryTitle(scrap.getCategory().getTitle())
                            .scrapTitle(scrap.getTitle())
                            .scrapURL(scrap.getScrapURL())
                            .imageURL(scrap.getImageURL())
                            .isFavorite(scrap.getIsFavorite())
                            .scrapDate(scrap.getCreatedAt().toLocalDate())
                            .build();
                })
                .toList();

        return SearchResponse.FindScrapDTO.builder()
                .meta(new Meta(scrapPage))
                .scrapDTOList(scrapDTOList)
                .build();
    }
}
