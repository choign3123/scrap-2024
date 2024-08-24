package com.example.scrap.web.scrap;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long>, JpaSpecificationExecutor<Scrap>{

    @Modifying
    @Query("DELETE FROM Scrap s WHERE s.member =:member")
    public void deleteAllByMember(@Param("member") Member member);

    Page<Scrap> findByMemberAndCategory(Member member, Category category, PageRequest pageRequest);

    Page<Scrap> findByMemberAndIsFavoriteIsTrue(Member member, PageRequest pageRequest);

    int countAllByMember(Member member);
}
