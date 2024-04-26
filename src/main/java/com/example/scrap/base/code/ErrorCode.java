package com.example.scrap.base.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode{

    // Common Error
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON000", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON001","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON002","권한이 잘못되었습니다"),
    _METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON003", "지원하지 않는 Http Method 입니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON004", "금지된 요청입니다."),

    // Member Error
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER001", "해당하는 사용자가 존재하지 않습니다."),

    // Category Error
    CATEGORY_MEMBER_NOT_MATCH(HttpStatus.NOT_FOUND, "CATEGORY001", "해당 카테고리에 접근할 권한이 없습니다."),
    NOT_ALLOWED_DELETE_DEFAULT_CATEGORY(HttpStatus.BAD_REQUEST, "CATEGORY002", "기본 카테고리는 삭제할 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String toString() {
        return code + ": " + message;
    }
}
