package com.example.scrap.web.mypage;

import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.jwt.ITokenProvider;
import com.example.scrap.web.member.dto.MemberDTO;
import com.example.scrap.web.mypage.dto.MypageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/mypage")
@RequiredArgsConstructor
@Tag(name = "마이페이지", description = "마이페이지 관련 API")
public class MypageController {

    private final IMypageQueryService mypageService;
    private final ITokenProvider tokenProvider;

    /**
     * [GET] /mypage
     * [API-23] 마이페이지 조회
     */
    @Operation(
            summary = "[API-23] 마이페이지 조회",
            security = { @SecurityRequirement(name = "bearer-key") },
            parameters = {@Parameter(name = "Authorization", example = "오른쪽 맨위 Authorize를 사용시, 여기엔 아무값이나 입력하세요")}
    )
    @GetMapping
    public ResponseEntity<ResponseDTO<MypageResponse.MypageDTO>> mypage(@RequestHeader(value = "Authorization") String token){
        MemberDTO memberDTO = tokenProvider.parseAccessToMemberDTO(token);

        MypageResponse.MypageDTO response = mypageService.mypage(memberDTO);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }
}
