package com.example.scrap.web.member;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySnsTypeAndSnsId(SnsType snsType, String snsId);

    @Query("SELECT m FROM Member m JOIN FETCH m.memberLog WHERE m.id =:id")
    Optional<Member> findByIdWithMemberLog(@Param("id") Long id);

    @Query("SELECT m FROM Member m JOIN FETCH m.memberLog WHERE m.snsId =:snsId AND m.snsType =:snsType")
    Optional<Member> findBySnsTypeAndSnsIdWithMemberLog(@Param("snsType") SnsType snsType, @Param("snsId") String snsId);
}
