package com.example.scrap.web.scrap;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import com.example.scrap.entity.enums.ScrapStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long>{

    Page<Scrap> findAllByMemberAndCategoryAndStatus(Member member, Category category, ScrapStatus status, PageRequest pageRequest);

    Page<Scrap> findAllByMemberAndFavoriteAndStatus(Member member, Boolean favorite, ScrapStatus status, PageRequest pageRequest);

    List<Scrap> findAllByMemberAndCategoryAndTitleContainingAndStatus(Member member, Category category, String title, ScrapStatus status, Sort sort);
}
