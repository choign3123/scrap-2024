package com.example.scrap.mypage;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.MemberLog;
import com.example.scrap.entity.enums.CategoryStatus;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.category.CategoryRepository;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.mypage.MypageQueryServiceImpl;
import com.example.scrap.web.mypage.dto.MypageResponse;
import com.example.scrap.web.scrap.ScrapRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MypageQueryServiceImplTest {

    @InjectMocks
    private MypageQueryServiceImpl mypageQueryService;

    @Mock
    private IMemberQueryService memberQueryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ScrapRepository scrapRepository;

    @DisplayName("마이페이지 조회")
    @Test
    public void mypage() {
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        when(memberQueryService.findMember(memberDTO)).thenReturn(member);
        when(categoryRepository.countByMemberAndStatus(member, CategoryStatus.ACTIVE)).thenReturn(7);
        when(scrapRepository.countAllByMember(member)).thenReturn(99);

        //** when
        MypageResponse.MypageDTO mypageDTO = mypageQueryService.mypage(memberDTO);

        //** then
        assertThat(mypageDTO.getMemberInfo().getName())
                .isEqualTo(member.getName());
        assertThat(mypageDTO.getStatistics().getTotalCategory())
                .isEqualTo(7L);
        assertThat(mypageDTO.getStatistics().getTotalScrap())
                .isEqualTo(99L);
    }

    private Member setupMember(){
        return Member.builder()
                .name("홍길동")
                .snsId("testSnsId")
                .snsType(SnsType.NAVER)
                .build();
    }

    private MemberDTO setupMemberDTO(Member member){
        return MemberDTO.builder()
                .memberId(member.getId())
                .snsId(member.getSnsId())
                .snsType(member.getSnsType())
                .build();
    }
}
