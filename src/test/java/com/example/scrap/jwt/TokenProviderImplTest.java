package com.example.scrap.jwt;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.data.DefaultData;
import com.example.scrap.base.exception.AuthorizationException;
import com.example.scrap.entity.Member;
import com.example.scrap.entity.MemberLog;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.jwt.dto.TokenType;
import com.example.scrap.web.member.IMemberQueryService;
import com.example.scrap.web.member.dto.MemberDTO;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenProviderImplTest {

    @Spy
    private TokenProviderImpl tokenProvider;

    private final String jwtSecretKey = System.getenv("JWT_SECRET");
    private final int expireHourOfAccessToken = Integer.parseInt(System.getenv("EXPIRE_HOUR_OF_ACCESS"));
    private final int expireDayOfRefreshToken = Integer.parseInt(System.getenv("EXPIRE_DAY_OF_REFRESH"));

    @BeforeEach
    @DisplayName("tokenProvider 환경 변수 주입")
    public void setupEnvironmentVariable() {
        ReflectionTestUtils.setField(tokenProvider, "jwtSecretKey", jwtSecretKey);
        ReflectionTestUtils.setField(tokenProvider, "expireHourOfAccessToken", expireHourOfAccessToken);
        ReflectionTestUtils.setField(tokenProvider, "expireDayOfRefreshToken", expireDayOfRefreshToken);
    }

    @DisplayName("토큰 발급하기")
    @Test
    public void createToken() {
        //** given
        Member member = setupMemberWithMemberLog();
        ReflectionTestUtils.setField(member, "id", 1L);

        //** when
        Token token = tokenProvider.createToken(member);

        //** then
        // access 토큰 검증
        assertThat(tokenProvider.equalsTokenType(token.getAccessToken(), TokenType.ACCESS)) // access 타입인지 확인
                .isTrue();
        assertThat(getTokenTTL(token.getAccessToken())) // 만료일 설정 잘 됐는지 확인
                .isEqualTo(pareHourToMs(expireHourOfAccessToken));
        assertThat(tokenProvider.isTokenValid(token.getAccessToken())) // 토큰 유효성 확인
                .isTrue();
        MemberDTO accessMemberDTO = tokenProvider.parseAccessToMemberDTO(token.getAccessToken());
        assertThat(accessMemberDTO.getMemberId().orElse(null))
                .isEqualTo(member.getId());
        assertThat(accessMemberDTO.getSnsType())
                .isEqualTo(member.getSnsType());
        assertThat(accessMemberDTO.getSnsId())
                .isEqualTo(member.getSnsId());

        // refresh 토큰 검증
        assertThat(tokenProvider.equalsTokenType(token.getRefreshToken(), TokenType.REFRESH)) // refresh 타입인지 확인
                .isTrue();
        assertThat(getTokenTTL(token.getRefreshToken())) // 만료일 설정 잘 됐는지 확인
                .isEqualTo(parseDayToMs(expireDayOfRefreshToken));
        assertThat(tokenProvider.isTokenValid(token.getRefreshToken())) // 토큰 유효성 확인
                .isTrue();
        assertThat(getRefreshTokenId(token.getRefreshToken())) // refreshTokenId 설정 됐는지 확인
                .isEqualTo(member.getMemberLog().getRefreshTokenId());
        MemberDTO refreshMemberDTO = tokenProvider.parseRefreshToMemberDTO(token.getRefreshToken());
        assertThat(refreshMemberDTO.getMemberId().isEmpty())
                .isTrue();
        assertThat(refreshMemberDTO.getSnsType())
                .isEqualTo(member.getSnsType());
        assertThat(refreshMemberDTO.getSnsId())
                .isEqualTo(member.getSnsId());
    }

    @DisplayName("토큰 갱신하기")
    @Test
    public void reissueToken() {
        //** given
        Member member = setupMemberWithMemberLog();
        ReflectionTestUtils.setField(member, "id", 1L);

        // refresh 토큰 설정
        String refreshToken = tokenProvider.createToken(member).getRefreshToken();
        Long prevRefreshTokenId = member.getMemberLog().getRefreshTokenId();

        //** when
        Token reissuedToken = tokenProvider.reissueToken(refreshToken, member);

        //** then
        // access 토큰 검증
        assertThat(tokenProvider.equalsTokenType(reissuedToken.getAccessToken(), TokenType.ACCESS)) // access 타입인지 확인
                .isTrue();
        assertThat(getTokenTTL(reissuedToken.getAccessToken())) // 만료일 설정 잘 됐는지 확인
                .isEqualTo(pareHourToMs(expireHourOfAccessToken));
        MemberDTO accessMemberDTO = tokenProvider.parseAccessToMemberDTO(reissuedToken.getAccessToken());
        assertThat(accessMemberDTO.getMemberId().orElse(null))
                .isEqualTo(member.getId());
        assertThat(accessMemberDTO.getSnsType())
                .isEqualTo(member.getSnsType());
        assertThat(accessMemberDTO.getSnsId())
                .isEqualTo(member.getSnsId());

        // refresh 토큰 검증
        assertThat(tokenProvider.equalsTokenType(reissuedToken.getRefreshToken(), TokenType.REFRESH)) // refresh 타입인지 확인
                .isTrue();
        assertThat(getTokenTTL(reissuedToken.getRefreshToken())) // 만료일 설정 잘 됐는지 확인
                .isEqualTo(parseDayToMs(expireDayOfRefreshToken));
        assertThat(getRefreshTokenId(reissuedToken.getRefreshToken())) // refreshTokenId 설정 됐는지 확인
                .isEqualTo(member.getMemberLog().getRefreshTokenId());
        MemberDTO refreshMemberDTO = tokenProvider.parseRefreshToMemberDTO(reissuedToken.getRefreshToken());
        assertThat(refreshMemberDTO.getMemberId().isEmpty())
                .isTrue();
        assertThat(refreshMemberDTO.getSnsType())
                .isEqualTo(member.getSnsType());
        assertThat(refreshMemberDTO.getSnsId())
                .isEqualTo(member.getSnsId());
        assertThat(member.getMemberLog().getRefreshTokenId()) // 이전과 다른 refreshTokenId로 설정되었는지 확인
                .isNotEqualTo(prevRefreshTokenId);
    }

    @DisplayName("[에러] 토큰 갱신하기 / refresh 토큰이 아닌 경우")
    @Test
    public void errorReissueToken_notRefreshToken() {
        //** given
        Member member = setupMemberWithMemberLog();
        ReflectionTestUtils.setField(member, "id", 1L);

        // access 토큰 설정
        String accessToken = tokenProvider.createToken(member).getAccessToken();

        //** when
        Throwable throwable = catchThrowable(() -> {
            tokenProvider.reissueToken(accessToken, member);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("refresh 토큰이 아님");
    }

    @DisplayName("[에러] 토큰 갱신하기 / 이미 사용된 refresh 토큰일 경우")
    @Test
    public void errorReissueToken_alreadyUsedRefreshToken() {
        //** given
        Member member = setupMemberWithMemberLog();
        ReflectionTestUtils.setField(member, "id", 1L);

        // refresh 토큰 설정
        String refreshToken = tokenProvider.createToken(member).getRefreshToken();

        // refresh 토큰 이미 사용된 것으로 처리하기
        Long newRefreshTokenId = member.getMemberLog().createRefreshTokenId();
        member.setRefreshTokenId(newRefreshTokenId);


        //** when
        Throwable throwable = catchThrowable(() -> {
            tokenProvider.reissueToken(refreshToken, member);
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining(ErrorCode.REFRESH_TOKEN_ALREADY_USED.getCode());
    }

    @DisplayName("토큰 유효성 검사")
    @Test
    public void isTokenValid() {
        //** given
        Member member = setupMemberWithMemberLog();
        ReflectionTestUtils.setField(member, "id", 1L);

        // 토큰 설정
        Token token = tokenProvider.createToken(member);

        //** when
        boolean result = tokenProvider.isTokenValid(token.getAccessToken());

        //** then
        assertThat(result)
                .isTrue();
    }

    @DisplayName("[에러] 토큰 유효성 검사 / 잘못된 토큰일 경우")
    @Test
    public void errorIsTokenValid_wrongToken() {
        //** given
        Member member = setupMemberWithMemberLog();
        ReflectionTestUtils.setField(member, "id", 1L);

        // 잘못된 토큰을 만들기 위한 jwtSecretKey 변경
        ReflectionTestUtils.setField(tokenProvider, "jwtSecretKey", "wrongJwtSecretKey");
        // 토큰 설정
        Token token = tokenProvider.createToken(member);

        // jwtSecretKey 원상 복귀
        ReflectionTestUtils.setField(tokenProvider, "jwtSecretKey", jwtSecretKey);

        //** when
        Throwable throwable = catchThrowable(() -> {
            tokenProvider.isTokenValid(token.getAccessToken());
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining(ErrorCode.TOKEN_NOT_VALID.getCode());
    }

    @DisplayName("[에러] 토큰 유효성 검사 / 만료된 토큰일 경우")
    @Test
    public void errorIsTokenValid_expiredToken() {
        //** given
        Member member = setupMemberWithMemberLog();
        ReflectionTestUtils.setField(member, "id", 1L);

        // 만료된 토큰을 만들기 위한 expireDay 변경
        ReflectionTestUtils.setField(tokenProvider, "expireHourOfAccessToken", 0);
        ReflectionTestUtils.setField(tokenProvider, "expireDayOfRefreshToken", 0);
        // 토큰 설정
        Token token = tokenProvider.createToken(member);

        // expireDay 원상 복귀
        ReflectionTestUtils.setField(tokenProvider, "expireHourOfAccessToken", expireHourOfAccessToken);
        ReflectionTestUtils.setField(tokenProvider, "expireDayOfRefreshToken", expireDayOfRefreshToken);

        //** when
        Throwable throwable = catchThrowable(() -> {
            tokenProvider.isTokenValid(token.getAccessToken());
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining(ErrorCode.TOKEN_EXPIRED.getCode());
    }

    @DisplayName("토큰 타입 검사 - O")
    @Test
    public void equalsTokenType_O() {
        //** given
        Member member = setupMemberWithMemberLog();
        ReflectionTestUtils.setField(member, "id", 1L);

        // 토큰 설정
        Token token = tokenProvider.createToken(member);

        //** when
        boolean result = tokenProvider.equalsTokenType(token.getAccessToken(), TokenType.ACCESS);

        //** then
        assertThat(result)
                .isTrue();
    }

    @DisplayName("토큰 타입 검사 - X")
    @Test
    public void equalsTokenType_X() {
        //** given
        Member member = setupMemberWithMemberLog();
        ReflectionTestUtils.setField(member, "id", 1L);

        // 토큰 설정
        Token token = tokenProvider.createToken(member);

        //** when
        boolean result = tokenProvider.equalsTokenType(token.getAccessToken(), TokenType.REFRESH);

        //** then
        assertThat(result)
                .isFalse();
    }

    @DisplayName("access 토큰을 MemberDTO로 변환하기")
    @Test
    public void parseAccessToMemberDTO() {
        //** given
        Member member = setupMemberWithMemberLog();
        ReflectionTestUtils.setField(member, "id", 1L);

        // 토큰 설정
        Token token = tokenProvider.createToken(member);

        //** when
        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token.getAccessToken());

        //** then
        assertThat(memberDTO.getMemberId().orElse(null))
                .isEqualTo(member.getId());
        assertThat(memberDTO.getSnsType())
                .isEqualTo(member.getSnsType());
        assertThat(memberDTO.getSnsId())
                .isEqualTo(member.getSnsId());
    }

    @DisplayName("[에러] access 토큰을 MemberDTO로 변환하기 / access 토큰이 아님")
    @Test
    public void errorParseAccessToMemberDTO_notAccessToken() {
        //** given
        Member member = setupMemberWithMemberLog();
        ReflectionTestUtils.setField(member, "id", 1L);

        // 토큰 설정
        Token token = tokenProvider.createToken(member);

        //** when
        Throwable throwable = catchThrowable(() -> {
            tokenProvider.parseAccessToMemberDTO(token.getRefreshToken());
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("access 토큰이 아님");
    }

    @DisplayName("refresh 토큰을 MemberDTO로 변환하기")
    @Test
    public void parseRefreshToMemberDTO() {
        //** given
        Member member = setupMemberWithMemberLog();
        ReflectionTestUtils.setField(member, "id", 1L);

        // 토큰 설정
        Token token = tokenProvider.createToken(member);

        //** when
        MemberDTO memberDTO = tokenProvider.parseRefreshToMemberDTO(token.getRefreshToken());

        //** then
        assertThat(memberDTO.getSnsType())
                .isEqualTo(member.getSnsType());
        assertThat(memberDTO.getSnsId())
                .isEqualTo(member.getSnsId());
    }

    @DisplayName("[에러] refresh 토큰을 MemberDTO로 변환하기 / refresh 토큰이 아님")
    @Test
    public void errorParseRefreshToMemberDTO_notRefreshToken() {
        //** given
        Member member = setupMemberWithMemberLog();
        ReflectionTestUtils.setField(member, "id", 1L);

        // 토큰 설정
        Token token = tokenProvider.createToken(member);

        //** when
        Throwable throwable = catchThrowable(() -> {
            tokenProvider.parseRefreshToMemberDTO(token.getAccessToken());
        });

        //** then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("refresh 토큰이 아님");
    }

    // TODO: parseMemberDTO 테스트 코드 추가 필요 - snsType이 맞지 않을 때

    @DisplayName("토큰 prefix 제거하기")
    @Test
    public void removeTokenPrefix() {
        //** given
        String token = DefaultData.AUTH_PREFIX + "testToken";

        //** when
        String updateToken = tokenProvider.removeTokenPrefix(token);

        //** then
        assertThat(updateToken)
                .isEqualTo("testToken");
    }

    private long getTokenTTL(String token){
        Date issuedAt = Jwts.parser().setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getIssuedAt();

        Date expireAt = Jwts.parser().setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        return expireAt.getTime() - issuedAt.getTime();
    }

    private long getRefreshTokenId(String refreshToken){
        return Long.parseLong(
                Jwts.parser().setSigningKey(jwtSecretKey)
                        .parseClaimsJws(refreshToken)
                        .getBody()
                        .getId()
        );
    }

    private long parseDayToMs(int day){
        return day * 1000 * 60 * 60 * 24L;
    }

    /**
     * hour를 밀리초로 변환
     */
    private long pareHourToMs(int hour) {
        return hour * 60L * 60L * 1000L; // 초, 분, 밀리단위
    }

    private Member setupMember(){
        return Member.builder()
                .name("홍길동")
                .snsId("testSnsId")
                .snsType(SnsType.NAVER)
                .build();
    }

    private Member setupMemberWithMemberLog(){
        Member member = Member.builder()
                .name("홍길동")
                .snsId("testSnsId")
                .snsType(SnsType.NAVER)
                .build();

        MemberLog memberLog = new MemberLog();

        ReflectionTestUtils.setField(member, "memberLog", memberLog);

        return member;
    }

    private MemberDTO setupMemberDTO(Member member){
        return MemberDTO.builder()
                .memberId(member.getId())
                .snsId(member.getSnsId())
                .snsType(member.getSnsType())
                .build();
    }
}
