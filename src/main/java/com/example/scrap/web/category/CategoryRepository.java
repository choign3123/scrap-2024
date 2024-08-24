package com.example.scrap.web.category;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 순서에 맞게 카테고리 조회
     */
    public List<Category> findAllByMemberAndStatusOrderBySequence(Member member, CategoryStatus status);

    @Modifying
    @Query("DELETE FROM Category c WHERE c.member =:member")
    public void deleteAllByMember(@Param("member") Member member);

    /**
     * 가장 높은 sequence를 가진 카테고리 찾기
     */
    @Query("SELECT MAX(c.sequence) FROM Category c WHERE c.member =:member and c.status =:status")
    public Optional<Integer> findMaxSequenceByMemberAndStatus(@Param("member") Member member, @Param("status") CategoryStatus status);


    /**
     * 총 카테고리 개수 조회
     */
    public int countByMemberAndStatus(Member member, CategoryStatus status);

    /**
     * 모든 카테고리 조회
     */
    public List<Category> findAllByMemberAndStatus(Member member, CategoryStatus status);
}
