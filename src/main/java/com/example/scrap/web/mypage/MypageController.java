package com.example.scrap.web.mypage;

import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.web.member.MemberDTO;
import com.example.scrap.web.mypage.dto.MypageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final IMypageService mypageService;

    /**
     * [GET] /mypage
     * [API-23] 마이페이지 조회
     * @param memberId
     * @return
     */
    @GetMapping
    public ResponseEntity<ResponseDTO> mypage(@RequestHeader("member-id") Long memberId){
        MemberDTO memberDTO = new MemberDTO(memberId);

        MypageResponse.MypageDTO response = mypageService.mypage(memberDTO);

        return ResponseEntity.ok(new ResponseDTO(response));
    }
}
