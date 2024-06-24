package com.example.scrap;

import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.TestTokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.manageMember.IMangeMemberService;
import com.example.scrap.web.member.dto.MemberDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Map;

@SpringBootTest
public class TestTokenProviderTests {

    @Autowired
    private TestTokenProvider testTokenProvider;

    @Autowired
    private IMangeMemberService mangeMemberService;

    private Map<String, String> tokenMap;

    @BeforeEach
    public void initTokenMap(){
        tokenMap = createTestToken();
    }

    @Test
    @DisplayName("테스트 토큰 발급")
    public Map<String, String> createTestToken(){

        Map<String, String> map = testTokenProvider.createTestToken(
                MemberDTO.builder()
                        .memberId(1L)
                        .snsId("test1234")
                        .snsType(SnsType.NAVER)
                        .build()
        );

        for(String key : map.keySet()){
            System.out.printf("%s: %s\n", key, map.get(key));
        }

        return map;
    }

    @Test
    @DisplayName("토큰 유효성 확인")
    public void checkTokenValid(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzbnNUeXBlIjoiTkFWRVIiLCJzbnNJZCI6InRlbXAxMjM0IiwiYXV" +
                "kIjoiMSIsInR5cGUiOiJBQ0NFU1MiLCJpYXQiOjE3MTkxMzIxMTgsImV4cCI6MTcxOTEzMzkxOH0.JhxKl5RnNDTW4VW4_Cu8Fsq3Ta5I0Yl9JXd5CSaGjWI";
        System.out.println("token valid: " + testTokenProvider.isTokenValid(token));
    }

    @Test
    @DisplayName("토큰 만료 시간 확인")
    public void checkTokenLiveTime(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzbnNUeXBlIjoiTkFWRVIiLCJzbnNJZCI6InRlbXAxMjM0IiwiYXVkIjoi" +
                "MSIsInR5cGUiOiJSRUZSRVNIIiwiaWF0IjoxNzE5MTUwNzcyLCJleHAiOjE3MTkxNTI1NzJ9.mVRZLUcL1Li2jGveROTdju9VeLVp1_JI2EXfvedng_M";

        try {
            System.out.println("현재 시간: " + new Date());
            System.out.println("토큰 만료 시간: " + testTokenProvider.getTokenExpireDate(token));
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("토큰 만료!");
        }

    }

    @Test
    @DisplayName("토큰 재발급 필요 여부")
    public void checkTokenRequiredReissue(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzbnNUeXBlIjoiTkFWRVIiLCJzbnNJZCI6In" +
                "RlbXAxMjM0IiwiYXVkIjoiMSIsInR5cGUiOiJSRUZSRVNIIiwiaWF0IjoxNzE5MTUwNzcyLCJleHAiOjE3MTkxNTI1NzJ9.mVRZLUcL1Li2jGveROTdju9VeLVp1_JI2EXfvedng_M";

        System.out.println("토큰 재발급 필요 여부: " + testTokenProvider.isRequiredTokenReissue(token));
    }

    @Test
    @DisplayName("access[O] refresh[O]")
    public void access유효_refresh_유효(){
        // given
        String accessToken = tokenMap.get("유효한 accessToken");
        String refreshToken = tokenMap.get("유효한 refreshToken");

        // when
        Token token = mangeMemberService.validateToken(accessToken, refreshToken);

        // then
        Assertions.assertThat(accessToken).isEqualTo(token.getAccessToken());
        Assertions.assertThat(refreshToken).isEqualTo(token.getRefreshToken());
    }

    @Test
    @DisplayName("access[O] refresh[△]")
    public void access유효_refresh갱신필요(){
        // given
        String accessToken = tokenMap.get("유효한 accessToken");
        String refreshToken = tokenMap.get("유효한 refreshToken (갱신 필요함)");

        // when
        Token token = mangeMemberService.validateToken(accessToken, refreshToken);

        // then
        Assertions.assertThat(accessToken).isEqualTo(token.getAccessToken());
        Assertions.assertThat(refreshToken).isNotEqualTo(token.getRefreshToken());
    }

    @Test
    @DisplayName("access[O] refresh[X]")
    public void access유효_refresh만료(){
        // given
        String accessToken = tokenMap.get("유효한 accessToken");
        String refreshToken = tokenMap.get("유효하지 않은 refreshToken");

        // when
        Token token = mangeMemberService.validateToken(accessToken, refreshToken);

        // then
        Assertions.assertThat(accessToken).isEqualTo(token.getAccessToken());
        Assertions.assertThat(refreshToken).isEqualTo(token.getRefreshToken());
    }

    @Test
    @DisplayName("access[△] refresh[O]")
    public void access갱신필요_refresh유효(){
        // given
        String accessToken = tokenMap.get("유효한 accessToken (갱신 필요함)");
        String refreshToken = tokenMap.get("유효한 refreshToken");

        // when
        Token token = mangeMemberService.validateToken(accessToken, refreshToken);

        // then
        Assertions.assertThat(accessToken).isNotEqualTo(token.getAccessToken());
        Assertions.assertThat(refreshToken).isEqualTo(token.getRefreshToken());
    }

    @Test
    @DisplayName("access[△] refresh[△]")
    public void access갱신필요_refresh갱신필요(){
        // given
        String accessToken = tokenMap.get("유효한 accessToken (갱신 필요함)");
        String refreshToken = tokenMap.get("유효한 refreshToken (갱신 필요함)");

        // when
        Token token = mangeMemberService.validateToken(accessToken, refreshToken);

        // then
        Assertions.assertThat(accessToken).isNotEqualTo(token.getAccessToken());
        Assertions.assertThat(refreshToken).isNotEqualTo(token.getRefreshToken());
    }

    @Test
    @DisplayName("access[△] refresh[X]")
    public void access갱신필요_refresh만료(){
        // given
        String accessToken = tokenMap.get("유효한 accessToken (갱신 필요함)");
        String refreshToken = tokenMap.get("유효하지 않은 refreshToken");

        // when
        Token token = mangeMemberService.validateToken(accessToken, refreshToken);

        // then
        Assertions.assertThat(accessToken).isNotEqualTo(token.getAccessToken());
        Assertions.assertThat(refreshToken).isEqualTo(token.getRefreshToken());
    }

    @Test
    @DisplayName("access[X] refresh[O]")
    public void access만료_refresh유효(){
        // given
        String accessToken = tokenMap.get("유효하지 않은 accessToken");
        String refreshToken = tokenMap.get("유효한 refreshToken");

        // when
        Token token = mangeMemberService.validateToken(accessToken, refreshToken);

        // then
        Assertions.assertThat(accessToken).isNotEqualTo(token.getAccessToken());
        Assertions.assertThat(refreshToken).isEqualTo(token.getRefreshToken());
    }

    @Test
    @DisplayName("access[X] refresh[△]")
    public void access만료_refresh갱신필요(){
        // given
        String accessToken = tokenMap.get("유효하지 않은 accessToken");
        String refreshToken = tokenMap.get("유효한 refreshToken (갱신 필요함)");

        // when
        Token token = mangeMemberService.validateToken(accessToken, refreshToken);

        // then
        Assertions.assertThat(accessToken).isNotEqualTo(token.getAccessToken());
        Assertions.assertThat(refreshToken).isEqualTo(token.getRefreshToken()); // 그러나 acessToken인 유효하지 않기에 갱신할 수 없음.
    }

    @Test
    @DisplayName("access[X] refresh[X]")
    public void access만료_refresh만료(){
        // given
        String accessToken = tokenMap.get("유효하지 않은 accessToken");
        String refreshToken = tokenMap.get("유효하지 않은 refreshToken");

        // then
        Assertions.assertThatThrownBy(
                () -> mangeMemberService.validateToken(accessToken, refreshToken)
        ).isInstanceOf(AuthorizationException.class);
    }

    @Test
    @DisplayName("access토큰이 아님")
    public void notAccessToken(){
        // given
        String accessToken = tokenMap.get("유효한 refreshToken");
        String refreshToken = tokenMap.get("유효한 refreshToken");

        // then
        Assertions.assertThatThrownBy(
                () -> mangeMemberService.validateToken(accessToken, refreshToken)
        ).isInstanceOf(AuthorizationException.class);
    }

    @Test
    @DisplayName("refresh토큰이 아님")
    public void notRefreshToken(){
        // given
        String accessToken = tokenMap.get("유효한 accessToken");
        String refreshToken = tokenMap.get("유효한 accessToken");

        // then
        Assertions.assertThatThrownBy(
                () -> mangeMemberService.validateToken(accessToken, refreshToken)
        ).isInstanceOf(AuthorizationException.class);
    }

    @Test
    @DisplayName("access의 멤버와 refresh의 멤버가 다름")
    public void accessMember_refreshMember_notMatch(){
        // given
        Map<String, String> otherMemberTokenMap = testTokenProvider.createTestToken(
                MemberDTO.builder()
                        .memberId(2L)
                        .snsId("9876test")
                        .snsType(SnsType.GOOGLE)
                        .build()
        );

        String accessToken = tokenMap.get("유효한 accessToken");
        String refreshToken = otherMemberTokenMap.get("유효한 refreshToken");

        // then
        Assertions.assertThatThrownBy(
                () -> mangeMemberService.validateToken(accessToken, refreshToken)
        ).isInstanceOf(AuthorizationException.class);
    }
}
