package com.example.scrap.web.test;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.base.response.ApiResponse;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.web.test.dto.TestRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/test")
@Slf4j
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

    @GetMapping("/valid")
    public ApiResponse testValid(@RequestBody @Valid TestRequest request){
        log.info("name: {}, age: {}", request.getName(), request.getAge());

        ResponseDTO<String> responseDTO = new ResponseDTO<>("성공했음");
        return new ApiResponse(responseDTO);
    }

    @GetMapping("/validator")
    public ApiResponse testValidator(@RequestBody @Valid TestRequest request){
        log.info("categoryId: {}", request.getCategoryId());

        ResponseDTO<String> responseDTO = new ResponseDTO<>("성공했음");
        return new ApiResponse(responseDTO);
    }
}
