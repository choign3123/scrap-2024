package com.example.scrap.web.category;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    public List<Category> findAllByMemberOrderBySequence(Member member);

    /**
     * 기본 카테고리 찾기
     */
    public Optional<Category> findByMemberAndIsDefaultTrue(Member member);

    @Modifying
    @Query("DELETE FROM Category c WHERE c.member =:member")
    public void deleteAllByMember(@Param("member") Member member);
}
