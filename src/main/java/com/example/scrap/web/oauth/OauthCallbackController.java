package com.example.scrap.web.oauth;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
@Hidden
public class OauthCallbackController {

    /**
     * 네이버 로그인 Callback API
     * Web 로그인을 위함.
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

    /**
     * 카카오 로그인 Callback API
     * (web 로그인용)
     */
    @GetMapping("kakao/callback")
    public String kakaoCallback(@RequestParam(required = false) String code,
                                @RequestParam(required = false) String error,
                                @RequestParam(required = false, name = "error_description") String errorDescription,
                                @RequestParam(required = false) String state){

        if(code != null){
            return code;
        }
        else{
            return error + ": " + errorDescription;
        }
    }
}
