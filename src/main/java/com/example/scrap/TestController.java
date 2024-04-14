package com.example.scrap;

import com.example.scrap.base.ApiResponse;
import com.example.scrap.base.ResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/ok")
    public ApiResponse testOK(){

        ResponseDTO<String> responseDTO = new ResponseDTO<>("성공했음");
        return new ApiResponse(responseDTO);
    }
}
