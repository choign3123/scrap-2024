package com.example.scrap.web.scrap;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    Page<Scrap> findAllByMemberAndCategoryOrderByCreatedAt(Member member, Category category, PageRequest pageRequest);
    Page<Scrap> findAllByMemberAndCategoryOrderByTitle(Member member, Category category, PageRequest pageRequest);

    Page<Scrap> findAllByMemberAndCategory(Member member, Category category, PageRequest pageRequest);
}
