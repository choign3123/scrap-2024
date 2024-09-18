package com.example.scrap.web.member;


import com.example.scrap.base.code.SuccessCode;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.MemberConverter;
import com.example.scrap.converter.OauthConverter;
import com.example.scrap.jwt.ITokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.member.dto.MemberResponse;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.oauth.dto.OauthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final IMemberCommandService memberCommandService;
    private final ITokenProvider tokenProvider; // TODO: query랑 command랑 메소드 분리 필요.

    /**
     * [POST] /oauth/naver/login
     * [API-33] 네이버 로그인(회원가입)
     * @param authorization
     * @return
     */
    @PostMapping("/oauth/login/naver")
    public ResponseEntity<ResponseDTO> naverLoginOrSignup(@RequestHeader("Authorization") String authorization){

        Token token = memberCommandService.login(authorization);

        OauthResponse.TokenDTO response = OauthConverter.toTokenDTO(token);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * [GET] /token
     * [API-28] 토큰 유효성 검사
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping("/token/me")
    public ResponseEntity<ResponseDTO> tokenValidate(@RequestHeader("Authorization") String token){

        return ResponseEntity.ok(new ResponseDTO<Void>(SuccessCode.TOKEN_VALID));
    }

    /**
     * [POST] /token
     * [API-34] 토큰 재발급
     */
    @PostMapping("/token")
    public ResponseEntity<ResponseDTO> tokenReissue(@RequestParam("refresh_token") String refreshToken){

        Token token = memberCommandService.reissueToken(refreshToken); // TODO: 여기서 token을 memberDTO로 변환해가야할 것 같음.
        MemberResponse.ReissueTokenDTO response = MemberConverter.toReissueTokenDTO(token);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [PATCH] /logout
     * [API-26] 로그아웃
     */
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PatchMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(@RequestHeader("Authorization") String token){
        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        memberCommandService.logout(memberDTO, token);

        return ResponseEntity.ok(new ResponseDTO<Void>());
    }

    /**
     * [PATCH] /signout
     * [API-5] 회원탈퇴
     */
    // TODO: patch -> delete로 변경하기
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PatchMapping("/signout")
    public ResponseEntity<ResponseDTO> signOut(@RequestHeader("Authorization") String token){
        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        memberCommandService.signOut(memberDTO);

        return ResponseEntity.ok(new ResponseDTO<Void>());
    }
}
