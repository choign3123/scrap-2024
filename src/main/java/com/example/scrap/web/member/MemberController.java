package com.example.scrap.web.member;


import com.example.scrap.base.code.SuccessCode;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.MemberConverter;
import com.example.scrap.jwt.TokenProvider;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.member.dto.MemberResponse;
import com.example.scrap.web.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final IMemberCommandService memberCommandService;
    private final TokenProvider tokenProvider;

    /**
     * [GET] /token
     * [API-28] 토큰 유효성 검사
     */
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

        Token token = memberCommandService.reissueToken(refreshToken);
        MemberResponse.ReissueTokenDTO response = MemberConverter.toReissueTokenDTO(token);

        return ResponseEntity.ok(new ResponseDTO(response));
    }

    /**
     * [PATCH] /logout
     * [API-26] 로그아웃
     */
    @PatchMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(@RequestHeader("Authorization") String token){
        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        memberCommandService.logout(memberDTO);

        return ResponseEntity.ok(new ResponseDTO<Void>());
    }

    /**
     * [PATCH] /signout
     * [API-5] 회원탈퇴
     */
    @PatchMapping("/signout")
    public ResponseEntity<ResponseDTO> signOut(@RequestHeader("Authorization") String token){
        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        memberCommandService.signOut(memberDTO);

        return ResponseEntity.ok(new ResponseDTO<Void>());
    }
}
