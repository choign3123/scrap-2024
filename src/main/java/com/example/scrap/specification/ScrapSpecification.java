package com.example.scrap.specification;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.entity.enums.ScrapStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

    public static Specification<Scrap> isAvailable(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), ScrapStatus.ACTIVE);
    }
}
