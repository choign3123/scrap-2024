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
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON005", "해당하는 요청을 찾을 수 없습니다."),

    // Authorization Error
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Authorization000", "토큰이 만료되었습니다"),
    TOKEN_NOT_VALID(HttpStatus.NOT_ACCEPTABLE, "Authorization002", "다시 로그인후 서비스 이용 바랍니다."),
    NOT_ACCESS_TOKEN(HttpStatus.NOT_ACCEPTABLE, "Authorization003", "다시 로그인후 서비스 이용 바랍니다."),
    NOT_REFRESH_TOKEN(HttpStatus.NOT_ACCEPTABLE, "Authorization004", "다시 로그인후 서비스 이용 바랍니다."),
    ACCESS_MEMBER_AND_REFRESH_MEMBER_NOT_MATCH(HttpStatus.NOT_ACCEPTABLE, "Authorization005", "다시 로그인후 서비스 이용 바랍니다."),
    TOKEN_VALUE_NOT_MATCH_TO_MEMBER(HttpStatus.NOT_ACCEPTABLE, "Authorization006", "다시 로그인후 서비스 이용 바랍니다."),
    INNER_TOKEN_VALUE_WRONG(HttpStatus.NOT_ACCEPTABLE, "Authorization009", "다시 로그인후 서비스 이용 바랍니다."), // 토큰에 든 값이 잘못된 경우
    REFRESH_TOKEN_ALREADY_USED(HttpStatus.NOT_ACCEPTABLE, "Authorization010", "이미 사용된 갱신 토큰입니다."),
    LOGOUT_TOKEN(HttpStatus.NOT_ACCEPTABLE, "Authorization011", "다시 로그인후 서비스 이용 바랍니다."),

    // Oauth Error
    OAUTH_NAVER_LOGIN_FAIL(HttpStatus.NOT_ACCEPTABLE, "Oauth001", "다시 네이버 로그인후 서비스 이용 바랍니다."),
    OAUTH_KAKAO_LOGIN_FAIL(HttpStatus.NOT_ACCEPTABLE, "Oauth002", "다시 카카오 로그인후 서비스 이용 바랍니다."),

    // Member Error
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER001", "해당하는 사용자가 존재하지 않습니다."),

    // MemberLog 에러

    // Category Error
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY001", "해당하는 카테고리가 존재하지 않습니다."),
    CATEGORY_MEMBER_NOT_MATCH(HttpStatus.BAD_REQUEST, "CATEGORY002", "해당 카테고리에 접근할 수 없습니다."),
    NOT_ALLOW_ACCESS_DEFAULT_CATEGORY(HttpStatus.BAD_REQUEST, "CATEGORY003", "기본 카테고리는 수정, 삭제할 수 없습니다."),
    REQUEST_CATEGORY_COUNT_NOT_ALL(HttpStatus.BAD_REQUEST, "CATEGORY004", "모든 카테고리에 대해 요청해주세요."),
    CATEGORY_MEMBER_NOT_MATCH_IN_SCRAP(HttpStatus.BAD_REQUEST, "CATEGORY005", "해당 스크랩에 접근할 수 없습니다."),
    DEFAULT_CATEGORY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "CATEGORY006", "기본 카테고리가 존재하지 않습니다."),
    EXCEED_CATEGORY_CREATE_LIMIT(HttpStatus.BAD_REQUEST, "CATEGORY007", "카테고리 최대 생성 개수를 초과했습니다."),
    DELETED_CATEGORY(HttpStatus.BAD_REQUEST, "CATEGORY008", "해당 카테고리에 접근할 수 없습니다."),

    // Scrap Error
    SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "SCRAP001", "해당하는 스크랩이 존재하지 않습니다."),
    SCRAP_MEMBER_NOT_MATCH(HttpStatus.BAD_REQUEST, "SCRAP002", "해당 스크랩에 접근할 수 없습니다."),
    SCRAP_CATEGORY_NOT_MATCH(HttpStatus.BAD_REQUEST, "SCRAP003", "해당 스크랩에 접근할 수 없습니다."),
    EXCEED_SCRAP_CREATE_LIMIT(HttpStatus.BAD_REQUEST, "SCRAP004", "스크랩 최대 생성 개수를 초과했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String toString() {
        return code + ": " + message;
    }
}
