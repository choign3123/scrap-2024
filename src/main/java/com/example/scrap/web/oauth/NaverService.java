package com.example.scrap.web.oauth;

import com.example.scrap.converter.MemberConverter;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.web.baseDTO.Token;
import com.example.scrap.web.category.ICategoryService;
import com.example.scrap.web.member.MemberRepository;
import com.example.scrap.web.oauth.dto.NaverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NaverService implements IOauthService{

    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final ICategoryService categoryService;
    private final static SnsType snsType = SnsType.NAVER;

    /**
     * 네이버 로그인
     * @param authorization
     * @return 회원가입이 되어있지 않은 회원의 경우, 자동 회원가입 후 token 반환
     */
    @Transactional
    public Token login(String authorization){
        // 네이버로부터 회원 정보 조회하기
        NaverResponse.ProfileInfo.Response profileInfo = callApi_GetProfileByAccessToken(authorization).getResponse();

        Optional<Member> optionalMember = memberRepository.findBySnsTypeAndSnsId(snsType, profileInfo.getId());

        if(optionalMember.isEmpty()){ // db에 없으면 해당 정보로 로그인 후, 토큰 생성해서 return
            Member member = signup(profileInfo);

            return Token.builder()
                    .accessToken(member.getName())
                    .build();
        }
        else { // db에 있으면 해당 정보로 토큰 생성해서 return

            return Token.builder()
                    .accessToken(optionalMember.get().getName())
                    .build();
        }
    }

    /**
     * 네이버 회원가입
     * @param profileInfo
     * @return
     */
    @Transactional
    public Member signup(NaverResponse.ProfileInfo.Response profileInfo){
        Member member = MemberConverter.toEntity(profileInfo, snsType);

        // 기본 카테고리 생성
        categoryService.createDefaultCategory(member);

        return memberRepository.save(member);
    }

    // 접근 토큰을 이용하여 프로필 API 호출하기
    /**
     * [TODO] 다른 외부 API를 호출할 수 있는 방법에 대해서도 고민해보기
     * https://jie0025.tistory.com/531
     * 지금은 외부 API를 호출하는 시점이 소셜 로그인을 할 때 뿐이라, 구현이 복잡하지 않은 기술을 택.
     */
    private NaverResponse.ProfileInfo callApi_GetProfileByAccessToken(String authorization){
        String url = "https://openapi.naver.com/v1/nid/me";
        authorization = "Bearer " + authorization;

        // header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authorization);

        // request entity 설정
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // API 호출
        ResponseEntity<NaverResponse.ProfileInfo> response = restTemplate.exchange(url, HttpMethod.GET, entity, NaverResponse.ProfileInfo.class);
        return response.getBody();
    }
}
