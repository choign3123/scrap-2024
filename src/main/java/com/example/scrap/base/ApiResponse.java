package com.example.scrap.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse<T extends ResponseDTO> extends ResponseEntity<T> {

    private T responseDTO;

    public ApiResponse(T responseDTO) {
        super(responseDTO, responseDTO.getHttpStatus());
        this.responseDTO = responseDTO;
    }
}
