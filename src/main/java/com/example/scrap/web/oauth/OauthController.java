package com.example.scrap.web.oauth;

import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.converter.OauthConverter;
import com.example.scrap.web.baseDTO.Token;
import com.example.scrap.web.oauth.dto.OauthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
public class OauthController {

    private final NaverService naverService;

    /**
     * [POST] /oauth/naver/login
     * [API-33] 네이버 로그인/회원가입
     * @param authorization
     * @return
     */
    @PostMapping("/naver/login")
    public ResponseEntity<ResponseDTO> naverLoginOrSignup(@RequestHeader("Authorization") String authorization){

        Token token = naverService.login(authorization);

        OauthResponse.TokenDTO response = OauthConverter.toTokenDTO(token);

        return ResponseEntity.ok(new ResponseDTO<>(response));
    }

    /**
     * 네이버 로그인 Callback API
     * Web 로그인을 위함.
     * @param code
     * @param state
     * @param error
     * @param errorDescription
     * @return
     */
    @GetMapping("/naver/callback")
    public String naverCallback(@RequestParam(required = false) String code, @RequestParam String state,
                              @RequestParam(required = false) String error, @RequestParam(name = "error_description", required = false) String errorDescription){

        log.info("code: {}", code);
        log.info("state: {}", state);
        log.info("error: {}", error);
        log.info("errorDescription: {}", errorDescription);

        return code;
    }
}
