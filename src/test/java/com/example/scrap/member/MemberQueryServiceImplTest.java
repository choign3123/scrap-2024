package com.example.scrap.member;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.member.MemberQueryServiceImpl;
import com.example.scrap.web.member.MemberRepository;
import com.example.scrap.web.member.dto.MemberDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberQueryServiceImplTest {

    @InjectMocks
    private MemberQueryServiceImpl memberQueryService;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("MemberDTO로 멤버 조회 - memberId로 조회")
    @Test
    public void findMemberByMemberDTO_memberId() {
        //** given
        Member member = setupMember();
        ReflectionTestUtils.setField(member, "id", 1L);
        MemberDTO memberDTO = setupMemberDTO(member);

        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        //** when
        Member findMember = memberQueryService.findMember(memberDTO);

        //** then
        assertThat(findMember)
                .isEqualTo(member);
    }

    @DisplayName("MemberDTO로 멤버 조회 - 소셜값으로 조회")
    @Test
    public void findMemberByMemberDTO_sns() {
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        when(memberRepository.findBySnsTypeAndSnsId(member.getSnsType(), member.getSnsId())).thenReturn(Optional.of(member));

        //** when
        Member findMember = memberQueryService.findMember(memberDTO);

        //** then
        assertThat(findMember)
                .isEqualTo(member);
    }

    @DisplayName("[에러] MemberDTO로 멤버 조회 - memberId로 조회 / 해당하는 멤버가 존재하지 않음")
    @Test
    public void errorFindMemberByMemberDTO_memberId_notFoundMember() {
        //** given
        Member member = setupMember();
        ReflectionTestUtils.setField(member, "id", 1L);
        MemberDTO memberDTO = setupMemberDTO(member);

        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());

        //** when
        Throwable throwable = catchThrowable(() -> {
            memberQueryService.findMember(memberDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("[에러] MemberDTO로 멤버 조회 - 소셜값으로 조회 / 해당하는 멤버가 존재하지 않음")
    @Test
    public void errorFindMemberByMemberDTO_sns_notFoundMember() {
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        when(memberRepository.findBySnsTypeAndSnsId(member.getSnsType(), member.getSnsId())).thenReturn(Optional.empty());

        //** when
        Throwable throwable = catchThrowable(() -> {
            memberQueryService.findMember(memberDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("소셜값으로 멤버 조회")
    @Test
    public void findMemberBySNS(){
        //** given
        Member member = setupMember();

        when(memberRepository.findBySnsTypeAndSnsId(member.getSnsType(), member.getSnsId())).thenReturn(Optional.of(member));

        //** when
        Member findMember = memberQueryService.findMember(member.getSnsId(), member.getSnsType());

        //** then
        assertThat(findMember)
                .isEqualTo(member);
    }

    @DisplayName("[에러] 소셜값으로 멤버 조회 / 해당하는 멤버가 존재하지 않음")
    @Test
    public void errorFindMemberBySNS_notFoundMember(){
        //** given
        Member member = setupMember();

        when(memberRepository.findBySnsTypeAndSnsId(member.getSnsType(), member.getSnsId())).thenReturn(Optional.empty());

        //** when
        Throwable throwable = catchThrowable(() -> {
            memberQueryService.findMember(member.getSnsId(), member.getSnsType());
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("MemberLog fetch join한 멤버 조회 - memberId로 조회")
    @Test
    public void findMemberByMemberDTOWithLog_memberId() {
        //** given
        Member member = setupMember();
        ReflectionTestUtils.setField(member, "id", 1L);
        MemberDTO memberDTO = setupMemberDTO(member);

        when(memberRepository.findByIdWithMemberLog(member.getId())).thenReturn(Optional.of(member));

        //** when
        Member findMember = memberQueryService.findMemberWithLog(memberDTO);

        //** then
        assertThat(findMember)
                .isEqualTo(member);
    }

    @DisplayName("MemberLog fetch join한 멤버 조회 - 소셜값으로 조회")
    @Test
    public void findMemberByMemberDTOWithLog_sns() {
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        when(memberRepository.findBySnsTypeAndSnsIdWithMemberLog(member.getSnsType(), member.getSnsId())).thenReturn(Optional.of(member));

        //** when
        Member findMember = memberQueryService.findMemberWithLog(memberDTO);

        //** then
        assertThat(findMember)
                .isEqualTo(member);
    }

    @DisplayName("[에러] MemberLog fetch join한 멤버 조회 - memberId로 조회 / 해당하는 멤버가 존재하지 않음")
    @Test
    public void errorFindMemberByMemberDTOWithLog_memberId_notFoundMember() {
        //** given
        Member member = setupMember();
        ReflectionTestUtils.setField(member, "id", 1L);
        MemberDTO memberDTO = setupMemberDTO(member);

        when(memberRepository.findByIdWithMemberLog(member.getId())).thenReturn(Optional.empty());

        //** when
        Throwable throwable = catchThrowable(() -> {
            memberQueryService.findMemberWithLog(memberDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("[에러] MemberLog fetch join한 멤버 조회 - 소셜값으로 조회 / 해당하는 멤버가 존재하지 않음")
    @Test
    public void errorFindMemberByMemberDTOWithLog_sns_notFoundMember() {
        //** given
        Member member = setupMember();
        MemberDTO memberDTO = setupMemberDTO(member);

        when(memberRepository.findBySnsTypeAndSnsIdWithMemberLog(member.getSnsType(), member.getSnsId())).thenReturn(Optional.empty());

        //** when
        Throwable throwable = catchThrowable(() -> {
            memberQueryService.findMemberWithLog(memberDTO);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
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
