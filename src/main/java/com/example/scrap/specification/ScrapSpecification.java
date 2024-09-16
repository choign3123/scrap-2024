package com.example.scrap.specification;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.base.enums.SearchScopeType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ScrapSpecification {

//    public static Specification<Scrap> equalTodoId(Long scrapId) {
//        return new Specification<Scrap>() {
//            @Override
//            public Predicate toPredicate(Root<Scrap> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//                // 1) equal
//                return criteriaBuilder.equal(root.get("scrapId"), scrapId);
//            }
//        };
//    }

    public static Specification<Scrap> equalMember(Member member){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("member"), member);
    }

    public static Specification<Scrap> equalCategory(Category category){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category"), category);
    }

    public static Specification<Scrap> containingTitle(String title){
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Scrap> containingMemo(String memo){
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("memo"), "%" + memo + "%");
    }

    public static Specification<Scrap> containingDescription(String description){
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("description"), "%" + description + "%");
    }

    public static Specification<Scrap> isFavorite(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isFavorite"), true);
    }

    public static Specification<Scrap> inCategory(List<Long> categoryIdList) {

        return (root, query, criteriaBuilder) -> criteriaBuilder.and(root.get("category").in(categoryIdList));
    }

    public static Specification<Scrap> inScrap(List<Long> scrapIdList) {

        return (root, query, criteriaBuilder) -> criteriaBuilder.and(root.get("id").in(scrapIdList));
    }

    public static Specification<Scrap> containingQueryInSearchType(String q, SearchScopeType searchScope){
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(searchScope.getName()), "%" + q + "%");
    }

    public static Specification<Scrap> betweenScrapDate(LocalDate startDate, LocalDate endDate){
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createdAt"), startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }
}
