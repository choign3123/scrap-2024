package com.example.scrap;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.base.response.ApiResponse;
import com.example.scrap.base.response.ResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/ok")
    public ApiResponse testOK(){

        ResponseDTO<String> responseDTO = new ResponseDTO<>("성공했음");
        return new ApiResponse(responseDTO);
    }

    @GetMapping("/ex/my")
    public ApiResponse testBaseException(@RequestParam("toggle") boolean toggle){

        if(toggle){
            throw new BaseException(ErrorCode._BAD_REQUEST);
        }
        else{
            ResponseDTO<String> responseDTO = new ResponseDTO<>("성공했음");
            return new ApiResponse(responseDTO);
        }
    }

    @GetMapping("/ex")
    public ApiResponse testException(@RequestParam("toggle") boolean toggle){

        if(toggle){
            throw new IllegalArgumentException("에러 테스트");
        }
        else{
            ResponseDTO<String> responseDTO = new ResponseDTO<>("성공했음");
            return new ApiResponse(responseDTO);
        }
    }
}
