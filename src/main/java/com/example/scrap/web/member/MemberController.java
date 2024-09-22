package com.example.scrap.web.member;


import com.example.scrap.base.code.SuccessCode;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.MemberConverter;
import com.example.scrap.entity.enums.SnsType;
import com.example.scrap.jwt.ITokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.validation.annotaion.EnumValid;
import com.example.scrap.web.member.dto.MemberResponse;
import com.example.scrap.web.member.dto.MemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "사용자", description = "사용자 관련 API")
public class MemberController {

    private final IMemberCommandService memberCommandService;
    private final ITokenProvider tokenProvider; // TODO: query랑 command랑 메소드 분리 필요.

    /**
     * [POST] /oauth/login?sns=
     * [API-33] 로그인(회원가입)
     */
    @Operation(
            summary = "[API-33] 로그인(회원가입)"
    )
    @PostMapping("/oauth/login")
    public ResponseEntity<ResponseDTO<MemberResponse.TokenDTO>>
    integrationLoginSignup(@RequestHeader("Authorization") String authorization,
                  @RequestParam @EnumValid(enumC = SnsType.class) String sns){

        // String -> Enum
        SnsType snsType = SnsType.valueOf(sns.toUpperCase());

        Token token = memberCommandService.integrationLoginSignup(authorization, snsType);
        MemberResponse.TokenDTO response = MemberConverter.toTokenDTO(token);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * [GET] /token
     * [API-28] 토큰 유효성 검사
     */
    @Operation(
            summary = "[API-28] 토큰 유효성 검사",
            security = { @SecurityRequirement(name = "bearer-key") },
            parameters = {@Parameter(name = "Authorization", example = "오른쪽 맨위 Authorize를 사용시, 여기엔 아무값이나 입력하세요")}
    )
    @GetMapping("/token/me")
    public ResponseEntity<ResponseDTO<Void>> tokenValidate(@RequestHeader("Authorization") String token){

        return ResponseEntity.ok(new ResponseDTO<>(SuccessCode.TOKEN_VALID));
    }

    /**
     * [POST] /token
     * [API-34] 토큰 재발급
     */
    @Operation(
            summary = "[API-34] 토큰 재발급"
    )
    @PostMapping("/token")
    public ResponseEntity<ResponseDTO<MemberResponse.ReissueTokenDTO>>
    tokenReissue(@RequestParam("refresh_token") String refreshToken){

        Token token = memberCommandService.reissueToken(refreshToken); // TODO: 여기서 token을 memberDTO로 변환해가야할 것 같음.
        MemberResponse.ReissueTokenDTO response = MemberConverter.toReissueTokenDTO(token);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * [PATCH] /logout
     * [API-26] 로그아웃
     */
    @Operation(
            summary = "[API-26] 로그아웃",
            security = { @SecurityRequirement(name = "bearer-key") },
            parameters = {@Parameter(name = "Authorization", example = "오른쪽 맨위 Authorize를 사용시, 여기엔 아무값이나 입력하세요")}
    )
    @PatchMapping("/logout")
    public ResponseEntity<ResponseDTO<Void>> logout(@RequestHeader("Authorization") String token){
        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        memberCommandService.logout(memberDTO, token);

        return ResponseEntity.ok(new ResponseDTO<>());
    }

    /**
     * [PATCH] /signout
     * [API-5] 회원탈퇴
     */
    // TODO: patch -> delete로 변경하기
    @Operation(
            summary = "[API-5] 회원탈퇴",
            security = { @SecurityRequirement(name = "bearer-key") },
            parameters = {@Parameter(name = "Authorization", example = "오른쪽 맨위 Authorize를 사용시, 여기엔 아무값이나 입력하세요")}
    )
    @PatchMapping("/signout")
    public ResponseEntity<ResponseDTO<Void>> signOut(@RequestHeader("Authorization") String token){
        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        memberCommandService.signOut(memberDTO);

        return ResponseEntity.ok(new ResponseDTO<>());
    }
}
