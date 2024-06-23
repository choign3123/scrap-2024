package com.example.scrap.web.manageMember;


import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.ManageMemberConverter;
import com.example.scrap.jwt.dto.Token;
import com.example.scrap.web.manageMember.dto.ManageMemberRequest;
import com.example.scrap.web.manageMember.dto.ManageMemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ManageMemberController {

    private IMangeMemberService mangeMemberService;

    /**
     * [POST] /me
     * [API-28] 토큰 유효성 검사
     */
    @PostMapping("/me")
    public ResponseEntity<ResponseDTO> tokenValidate(@RequestBody @Validated ManageMemberRequest.ValidateTokenDTO request){

        Token token = mangeMemberService.validateToken(request);

        ManageMemberResponse.ValidateTokenDTO response = ManageMemberConverter.toValidateTokenDTO(token);

        return ResponseEntity.ok(new ResponseDTO(response));
    }
}
