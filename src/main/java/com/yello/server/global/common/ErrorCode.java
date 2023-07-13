package com.yello.server.global.common;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    /**
     * 400 BAD REQUEST
     */
    REQUEST_VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    YELLOID_REQUIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "yelloId를 쿼리 스트링으로 보내주세요."),
    OAUTH_ACCESS_TOKEN_REQUIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "OAuth accessToken이 없습니다."),
    LACK_USER_EXCEPTION(HttpStatus.BAD_REQUEST, "친구가 4명 이하입니다."),
    SIGNIN_FIELD_REQUIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "회원가입에 필요한 값이 없습니다. 명세서를 자세히 확인해주세요. >_*"),
    FIELD_REQUIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "필요한 값이 없습니다."),

    //friend
    EXIST_FRIEND_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 존재하는 친구입니다."),

    /**
     * 401 UNAUTHORIZED
     */
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 비어있습니다."),
    OAUTH_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "잘못된 소셜 토큰입니다."),
    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, "잘못된 인증 요청입니다."),

    /**
     * 403 FORBIDDEN
     */
    NOT_SIGNIN_USER_EXCEPTION(HttpStatus.FORBIDDEN, "가입하지 않은 회원입니다."),

    /**
     * 404 NOT FOUND
     */
    YELLOID_NOT_FOUND_USER_EXCEPTION(HttpStatus.NOT_FOUND, "해당 YELLOID의 유저가 존재하지 않습니다."),
    AUTH_NOT_FOUND_USER_EXCEPTION(HttpStatus.NOT_FOUND, "토큰 정보에 해당하는 유저가 존재하지 않습니다."),
    GROUPID_NOT_FOUND_GROUP_EXCEPTION(HttpStatus.NOT_FOUND, "해당 그룹 id의 그룹이 존재하지 않습니다."),
    USERID_NOT_FOUND_USER_EXCEPTION(HttpStatus.NOT_FOUND, "해당 유저 id의 유저가 존재하지 않습니다."),
    TOKEN_TIME_EXPIRED_EXCEPTION(HttpStatus.UNAUTHORIZED, "인증되지 않은 유저입니다."),
    INVALID_PASSWORD_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않은 비밀번호입니다."),
    NOT_FOUND_SAVE_IMAGE_EXCEPTION(HttpStatus.NOT_FOUND, "이미지가 저장되지 않았습니다."),
    NOT_FOUND_IMAGE_EXCEPTION(HttpStatus.NOT_FOUND, "이미지가 없습니다."),
    NOT_FOUND_VOTE_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 투표입니다."),
    NOT_FOUND_QUESTION_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 질문입니다."),
    /**
     * 409 CONFLICT
     */
    ALREADY_EXIST_USER_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 유저입니다"),
    YELLOID_CONFLICT_USER_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 yelloId 입니다."),
    UUID_CONFLICT_USER_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 소셜로그인 유저입니다."),

    /**
     * 500 INTERNAL SERVER ERROR
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러가 발생했습니다"),
    REDIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis에 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}