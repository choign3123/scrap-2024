package com.example.scrap.web.test;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.base.response.ResponseDTO;
import com.example.scrap.web.test.dto.TestRequest;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/test")
@Slf4j
@Hidden
public class TestController {

    @GetMapping("/ok")
    public ResponseEntity<ResponseDTO> testOK(){

        ResponseDTO<String> responseDTO = new ResponseDTO<>("성공했음");
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/ex/my")
    public ResponseEntity<ResponseDTO> testBaseException(@RequestParam("toggle") boolean toggle){

        if(toggle){
            throw new BaseException(ErrorCode._BAD_REQUEST);
        }
        else{
            ResponseDTO<String> responseDTO = new ResponseDTO<>("성공했음");
            return ResponseEntity.ok(responseDTO);
        }
    }

    @GetMapping("/ex")
    public ResponseEntity<ResponseDTO> testException(@RequestParam("toggle") boolean toggle){

        if(toggle){
            throw new IllegalArgumentException("에러 테스트");
        }
        else{
            ResponseDTO<String> responseDTO = new ResponseDTO<>("성공했음");
            return ResponseEntity.ok(responseDTO);
        }
    }

    @GetMapping("/valid")
    public ResponseEntity<ResponseDTO> testValid(@RequestBody @Valid TestRequest request){
        log.info("name: {}, age: {}", request.getName(), request.getAge());

        ResponseDTO<String> responseDTO = new ResponseDTO<>("성공했음");
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/validator")
    public ResponseEntity<ResponseDTO> testValidator(@RequestBody @Valid TestRequest request){
        log.info("categoryId: {}", request.getCategoryId());

        ResponseDTO<String> responseDTO = new ResponseDTO<>("성공했음");
        return ResponseEntity.ok(responseDTO);
    }
}
