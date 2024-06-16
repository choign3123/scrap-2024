package com.example.scrap.web.member;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // [TODO] fetch join으로 성능 향상 꿰하기
    Optional<Member> findBySnsTypeAndSnsId(SnsType snsType, String snsId);
}
