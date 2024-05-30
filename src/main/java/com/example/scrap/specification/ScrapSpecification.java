package com.example.scrap.specification;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.entity.enums.ScrapStatus;
import com.example.scrap.web.baseDTO.SearchScopeType;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public static Specification<Scrap> isAvailable(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), ScrapStatus.ACTIVE);
    }

    public static Specification<Scrap> isFavorite(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("favorite"), true);
    }

    public static Specification<Scrap> inCategory(List<Long> categoryIdList) {

        return (root, query, criteriaBuilder) -> criteriaBuilder.and(root.get("category").in(categoryIdList));
    }

    public static Specification<Scrap> containingQueryInSearchType(String q, SearchScopeType searchScope){
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(searchScope.getName()), "%" + q + "%");
    }

    public static Specification<Scrap> betweenScrapDate(LocalDate startDate, LocalDate endDate){
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createdAt"), startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }
}
