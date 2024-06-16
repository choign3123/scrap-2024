package com.example.scrap.web.member;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.converter.MemberConverter;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.category.ICategoryCommandService;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.oauth.dto.NaverResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements IMemberQueryService {

    private final MemberRepository memberRepository;
    private final ICategoryCommandService categoryCommandService;

    /**
     * 멤버 조회
     * @param memberId 멤버 식별자
     * @return
     */
    public Member findMember(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 멤버 조회
     * @param snsType
     * @param snsId
     * @return
     */
    public Member findMember(SnsType snsType, String snsId){
        
        // [TODO] 회원탈퇴한 멤버는 아닌지 확인하는 로직 필요
        return memberRepository.findBySnsTypeAndSnsId(snsType, snsId)
                .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 멤버 조회
     * @param memberDTO 멤버 식별자
     * @return
     */
    public Member findMember(MemberDTO memberDTO){
        if(memberDTO.getMemberId() != null){
            return findMember(memberDTO.getMemberId());
        }
        else if(memberDTO.getSnsId() != null && memberDTO.getSnsType() != null){
            return findMember(memberDTO.getSnsType(), memberDTO.getSnsId());
        }
        else{
            throw new BaseException(ErrorCode._BAD_REQUEST);
        }
    }

    /**
     * 네이버 회원가입
     * @param profileInfo
     * @return
     */
    @Transactional
    public Member signup(NaverResponse.ProfileInfo.Response profileInfo){
        Member member = MemberConverter.toEntity(profileInfo, SnsType.NAVER);

        // 기본 카테고리 생성
        categoryCommandService.createDefaultCategory(member);

        return memberRepository.save(member);
    }
}
