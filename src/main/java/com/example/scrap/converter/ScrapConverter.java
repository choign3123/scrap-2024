package com.example.scrap.converter;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.web.baseDTO.Meta;
import com.example.scrap.web.scrap.dto.ScrapRequest;
import com.example.scrap.web.scrap.dto.ScrapResponse;
import org.springframework.data.domain.Page;

import java.util.List;

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
                .isFavorite(scrap.getFavorite())
                .scrapDate(scrap.getCreatedAt().toLocalDate())
                .build();
    }

    public static ScrapResponse.GetScrapListByCategory toGetScrapListByCategory(Page<Scrap> scrapPage){
        Meta meta = new Meta(scrapPage);

        List<ScrapResponse.GetScrapListByCategory.ScrapDTO> scrapDTOList = scrapPage.stream()
                .map(scrap -> {
                    return ScrapResponse.GetScrapListByCategory.ScrapDTO.builder()
                            .scrapId(scrap.getId())
                            .title(scrap.getTitle())
                            .scrapURL(scrap.getScrapURL())
                            .imageURL(scrap.getImageURL())
                            .isFavorite(scrap.getFavorite())
                            .scrapDate(scrap.getCreatedAt().toLocalDate())
                            .build();
                })
                .toList();

        return ScrapResponse.GetScrapListByCategory.builder()
                .meta(meta)
                .scrapDTOList(scrapDTOList)
                .build();
    }

    public static ScrapResponse.GetScrapDetails toGetScrapDetails(Scrap scrap){
        return ScrapResponse.GetScrapDetails.builder()
                .scrapId(scrap.getId())
                .title(scrap.getTitle())
                .scrapURL(scrap.getScrapURL())
                .imageURL(scrap.getImageURL())
                .description(scrap.getDescription())
                .memo(scrap.getMemo())
                .isFavorite(scrap.getFavorite())
                .build();
    }

    public static ScrapResponse.GetFavoriteScrapList toGetFavoriteScrapList(Page<Scrap> scrapPage){
        Meta meta = new Meta(scrapPage);

        List<ScrapResponse.GetFavoriteScrapList.ScrapDTO> scrapDTOList = scrapPage.stream()
                .map( scrap -> {
                    return ScrapResponse.GetFavoriteScrapList.ScrapDTO.builder()
                            .categoryTitle(scrap.getCategory().getTitle())
                            .scrapId(scrap.getId())
                            .scrapTitle(scrap.getTitle())
                            .scrapURL(scrap.getScrapURL())
                            .imageURL(scrap.getImageURL())
                            .scrapDate(scrap.getCreatedAt().toLocalDate())
                            .build();
                })
                .toList();

        return ScrapResponse.GetFavoriteScrapList.builder()
                .meta(meta)
                .scrapDTOList(scrapDTOList)
                .build();
    }

    public static ScrapResponse.FindScrapByTitle toFindScrapByTitle(List<Scrap> scrapList){
        List<ScrapResponse.FindScrapByTitle.ScrapDTO> scrapDTOList = scrapList.stream()
                .map( scrap -> {
                    return ScrapResponse.FindScrapByTitle.ScrapDTO.builder()
                            .scrapId(scrap.getId())
                            .title(scrap.getTitle())
                            .scrapURL(scrap.getScrapURL())
                            .imageURL(scrap.getImageURL())
                            .isFavorite(scrap.getFavorite())
                            .scrapDate(scrap.getCreatedAt().toLocalDate())
                            .build();
                })
                .toList();

        return ScrapResponse.FindScrapByTitle.builder()
                .scrapDTOList(scrapDTOList)
                .total(scrapList.size())
                .build();
    }

    public static ScrapResponse.ToggleScrapFavorite toToggleScrapFavorite(Scrap scrap){

        return ScrapResponse.ToggleScrapFavorite.builder()
                .isFavorite(scrap.getFavorite())
                .build();
    }

    public static ScrapResponse.ToggleScrapFavoriteList toToggleScrapFavoriteList(List<Scrap> scrapList){

        List<ScrapResponse.ToggleScrapFavoriteList.ScrapDTO> scrapDTOList = scrapList.stream()
                .map(scrap -> {
                    return ScrapResponse.ToggleScrapFavoriteList.ScrapDTO.builder()
                            .scrapId(scrap.getId())
                            .isFavorite(scrap.getFavorite())
                            .build();
                })
                .toList();

        return ScrapResponse.ToggleScrapFavoriteList.builder()
                .scrapDTOList(scrapDTOList)
                .total(scrapList.size())
                .build();
    }

    public static ScrapResponse.UpdateScrapMemo toUpdateScrapMemo(Scrap scrap){

        return ScrapResponse.UpdateScrapMemo.builder()
                .scrapId(scrap.getId())
                .memo(scrap.getMemo())
                .build();
    }
}
