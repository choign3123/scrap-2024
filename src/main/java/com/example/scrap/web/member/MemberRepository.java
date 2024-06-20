package com.example.scrap.web.member;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.mypage.dto.MypageResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findBySnsTypeAndSnsId(SnsType snsType, String snsId);

    // [TODO] 좀더 유지보수하기 좋은 코드가 없는지 고민 필요
    @Query("SELECT new com.example.scrap.web.mypage.dto.MypageResponse$MypageDTO$Statistics(count(c), count(s)) " + // inner 클래스의경우 .이 아닌 $로 표시
            "FROM Category c " +
            "LEFT jOIN c.scrapList s " +
            "WHERE c.member =:member " +
            "AND (s.status = 'ACTIVE' OR s.status is NULL)")
    MypageResponse.MypageDTO.Statistics getMypageStatistics(@Param("member") Member member);
}
