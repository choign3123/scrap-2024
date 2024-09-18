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

    public static ScrapResponse.GetScrapListByCategoryDTO toGetScrapListByCategory(Page<Scrap> scrapPage){
        Meta meta = new Meta(scrapPage);

        List<ScrapResponse.GetScrapListByCategoryDTO.ScrapDTO> scrapDTOList = scrapPage.stream()
                .map(scrap -> {
                    return ScrapResponse.GetScrapListByCategoryDTO.ScrapDTO.builder()
                            .scrapId(scrap.getId())
                            .title(scrap.getTitle())
                            .scrapURL(scrap.getScrapURL())
                            .imageURL(scrap.getImageURL())
                            .isFavorite(scrap.getIsFavorite())
                            .scrapDate(scrap.getCreatedAt().toLocalDate())
                            .build();
                })
                .toList();

        return ScrapResponse.GetScrapListByCategoryDTO.builder()
                .meta(meta)
                .scrapDTOList(scrapDTOList)
                .build();
    }

    public static ScrapResponse.GetScrapDetailsDTO toGetScrapDetails(Scrap scrap){
        return ScrapResponse.GetScrapDetailsDTO.builder()
                .scrapId(scrap.getId())
                .title(scrap.getTitle())
                .scrapURL(scrap.getScrapURL())
                .imageURL(scrap.getImageURL())
                .description(scrap.getDescription())
                .memo(scrap.getMemo())
                .isFavorite(scrap.getIsFavorite())
                .build();
    }

    public static ScrapResponse.GetFavoriteScrapListDTO toGetFavoriteScrapList(Page<Scrap> scrapPage){
        Meta meta = new Meta(scrapPage);

        List<ScrapResponse.GetFavoriteScrapListDTO.ScrapDTO> scrapDTOList = scrapPage.stream()
                .map( scrap -> {
                    return ScrapResponse.GetFavoriteScrapListDTO.ScrapDTO.builder()
                            .categoryTitle(scrap.getCategory().getTitle()) // [TODO] LazyInitializationException이 실제로 발생하는지 확인해볼 필요 있음.
                            .scrapId(scrap.getId())
                            .scrapTitle(scrap.getTitle())
                            .scrapURL(scrap.getScrapURL())
                            .imageURL(scrap.getImageURL())
                            .scrapDate(scrap.getCreatedAt().toLocalDate())
                            .build();
                })
                .toList();

        return ScrapResponse.GetFavoriteScrapListDTO.builder()
                .meta(meta)
                .scrapDTOList(scrapDTOList)
                .build();
    }

    public static ScrapResponse.FindScrapAtParticularCategoryDTO toFindScrapAtParticularCategory(List<Scrap> scrapList){
        List<ScrapResponse.FindScrapAtParticularCategoryDTO.ScrapDTO> scrapDTOList = scrapList.stream()
                .map( scrap -> {
                    return ScrapResponse.FindScrapAtParticularCategoryDTO.ScrapDTO.builder()
                            .scrapId(scrap.getId())
                            .title(scrap.getTitle())
                            .scrapURL(scrap.getScrapURL())
                            .imageURL(scrap.getImageURL())
                            .isFavorite(scrap.getIsFavorite())
                            .scrapDate(scrap.getCreatedAt().toLocalDate())
                            .build();
                })
                .toList();

        return ScrapResponse.FindScrapAtParticularCategoryDTO.builder()
                .scrapDTOList(scrapDTOList)
                .total(scrapList.size())
                .build();
    }

    public static ScrapResponse.FindScrapAtFavoriteDTO toFindScrapAtFavorite(List<Scrap> scrapList){
        List<ScrapResponse.FindScrapAtFavoriteDTO.ScrapDTO> scrapDTOList = scrapList.stream()
                .map( scrap -> {
                    return ScrapResponse.FindScrapAtFavoriteDTO.ScrapDTO.builder()
                            .scrapId(scrap.getId())
                            .title(scrap.getTitle())
                            .scrapURL(scrap.getScrapURL())
                            .imageURL(scrap.getImageURL())
                            .isFavorite(scrap.getIsFavorite())
                            .scrapDate(scrap.getCreatedAt().toLocalDate())
                            .build();
                })
                .toList();

        return ScrapResponse.FindScrapAtFavoriteDTO.builder()
                .scrapDTOList(scrapDTOList)
                .total(scrapList.size())
                .build();
    }

    public static ScrapResponse.ToggleScrapFavoriteDTO toToggleScrapFavorite(Scrap scrap){

        return ScrapResponse.ToggleScrapFavoriteDTO.builder()
                .scrapId(scrap.getId())
                .isFavorite(scrap.getIsFavorite())
                .build();
    }

    public static ScrapResponse.ToggleScrapFavoriteListDTO toToggleScrapFavoriteList(List<Scrap> scrapList){

        List<ScrapResponse.ToggleScrapFavoriteListDTO.ScrapDTO> scrapDTOList = scrapList.stream()
                .map(scrap -> {
                    return ScrapResponse.ToggleScrapFavoriteListDTO.ScrapDTO.builder()
                            .scrapId(scrap.getId())
                            .isFavorite(scrap.getIsFavorite())
                            .build();
                })
                .toList();

        return ScrapResponse.ToggleScrapFavoriteListDTO.builder()
                .scrapDTOList(scrapDTOList)
                .total(scrapList.size())
                .build();
    }

    public static ScrapResponse.UpdateScrapMemoDTO toUpdateScrapMemo(Scrap scrap){

        return ScrapResponse.UpdateScrapMemoDTO.builder()
                .scrapId(scrap.getId())
                .memo(scrap.getMemo())
                .build();
    }

    public static ScrapResponse.MoveCategoryOfScrapDTO toMoveCategoryOfScrap(Scrap scrap){

        return ScrapResponse.MoveCategoryOfScrapDTO.builder()
                .scrapId(scrap.getId())
                .categoryId(scrap.getCategory().getId())
                .build();
    }

    public static ScrapResponse.MoveCategoryOfScrapListDTO toMoveCategoryOfScrapList(List<Scrap> scrapList){

        List<ScrapResponse.MoveCategoryOfScrapListDTO.ScrapDTO> scrapDTOList = scrapList.stream()
                .map(scrap -> {
                    return ScrapResponse.MoveCategoryOfScrapListDTO.ScrapDTO.builder()
                            .scrapId(scrap.getId())
                            .categoryId(scrap.getCategory().getId())
                            .build();
                })
                .toList();

        return ScrapResponse.MoveCategoryOfScrapListDTO.builder()
                .total(scrapList.size())
                .scrapDTOList(scrapDTOList)
                .build();
    }

    // TODO: 삭제하기
    public static ScrapResponse.ShareAllScrapDTO toShareAllScrap(List<Scrap> scrapList){

        List<ScrapResponse.ShareAllScrapDTO.ScrapDTO> scrapDTOList = scrapList.stream()
                .map(scrap -> {
                    return ScrapResponse.ShareAllScrapDTO.ScrapDTO.builder()
                            .scrapId(scrap.getId())
                            .title(scrap.getTitle())
                            .scrapURL(scrap.getScrapURL())
                            .build();
                })
                .toList();

        return ScrapResponse.ShareAllScrapDTO.builder()
                .scrapDTOList(scrapDTOList)
                .total(scrapList.size())
                .build();
    }
}
