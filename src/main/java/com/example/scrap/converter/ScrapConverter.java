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

    public static Scrap toEntity(ScrapRequest.CreateScrapDTO request, Member member, Category category){
        return Scrap.builder()
                .scrapURL(request.getScrapURL())
                .imageURL(request.getImageURL())
                .title(request.getTitle())
                .description(request.getDescription())
                .memo(request.getMemo())
                .member(member)
                .category(category)
                .isFavorite(request.getIsFavorite())
                .build();
    }

    public static ScrapResponse.CreateScrapDTO toCreateScrapDTO(Scrap scrap){
        return ScrapResponse.CreateScrapDTO.builder()
                .scrapId(scrap.getId())
                .title(scrap.getTitle())
                .scrapURL(scrap.getScrapURL())
                .imageURL(scrap.getImageURL())
                .isFavorite(scrap.getIsFavorite())
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
                            .isFavorite(scrap.getIsFavorite())
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
                .isFavorite(scrap.getIsFavorite())
                .build();
    }

    public static ScrapResponse.GetFavoriteScrapList toGetFavoriteScrapList(Page<Scrap> scrapPage){
        Meta meta = new Meta(scrapPage);

        List<ScrapResponse.GetFavoriteScrapList.ScrapDTO> scrapDTOList = scrapPage.stream()
                .map( scrap -> {
                    return ScrapResponse.GetFavoriteScrapList.ScrapDTO.builder()
                            .categoryTitle(scrap.getCategory().getTitle()) // [TODO] LazyInitializationException이 실제로 발생하는지 확인해볼 필요 있음.
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
                            .isFavorite(scrap.getIsFavorite())
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
                .scrapId(scrap.getId())
                .isFavorite(scrap.getIsFavorite())
                .build();
    }

    public static ScrapResponse.ToggleScrapFavoriteList toToggleScrapFavoriteList(List<Scrap> scrapList){

        List<ScrapResponse.ToggleScrapFavoriteList.ScrapDTO> scrapDTOList = scrapList.stream()
                .map(scrap -> {
                    return ScrapResponse.ToggleScrapFavoriteList.ScrapDTO.builder()
                            .scrapId(scrap.getId())
                            .isFavorite(scrap.getIsFavorite())
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

    public static ScrapResponse.MoveCategoryOfScrap toMoveCategoryOfScrap(Scrap scrap){

        return ScrapResponse.MoveCategoryOfScrap.builder()
                .scrapId(scrap.getId())
                .categoryId(scrap.getCategory().getId())
                .build();
    }

    public static ScrapResponse.MoveCategoryOfScraps toMoveCategoryOfScraps(List<Scrap> scrapList){

        List<ScrapResponse.MoveCategoryOfScraps.ScrapDTO> scrapDTOList = scrapList.stream()
                .map(scrap -> {
                    return ScrapResponse.MoveCategoryOfScraps.ScrapDTO.builder()
                            .scrapId(scrap.getId())
                            .categoryId(scrap.getCategory().getId())
                            .build();
                })
                .toList();

        return ScrapResponse.MoveCategoryOfScraps.builder()
                .total(scrapList.size())
                .scrapDTOList(scrapDTOList)
                .build();
    }
}
