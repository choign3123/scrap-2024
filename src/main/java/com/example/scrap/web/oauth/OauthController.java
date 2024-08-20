package com.example.scrap.web.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
public class OauthController {

    private final NaverProvider naverProvider;

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
