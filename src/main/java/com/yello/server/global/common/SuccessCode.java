package com.yello.server.global.common;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {
    /**
     * 200 OK
     */
    READ_VOTE_SUCCESS(OK, "투표 조회에 성공했습니다."),
    YELLOID_VALIDATION_SUCCESS(OK, "옐로 아이디 중복 여부 조회에 성공하였습니다."),
    READ_FRIEND_SUCCESS(OK, "친구 조회에 성공했습니다."),
    READ_USER_SUCCESS(OK, "유저 조회에 성공했습니다."),
    ADD_FRIEND_SUCCESS(OK, "친구 추가에 성공했습니다."),
    SHUFFLE_FRIEND_SUCCESS(OK, "친구 셔플에 성공했습니다."),
    CHECK_KEYWORD_SUCCESS(OK, "해당 키워드 확인하는데 성공했습니다."),
    READ_YELLO_VOTE_SUCCESS(OK, "옐로 투표 리스트 조회에 성공했습니다."),
    READ_YELLO_START_SUCCESS(OK, "옐로 시작하기에 성공했습니다."),
    DELETE_USER_SUCCESS(OK, "유저 탈퇴에 성공했습니다."),
    DELETE_FRIEND_SUCCESS(OK, "친구 삭제에 성공했습니다."),
    ONBOARDING_FRIENDS_SUCCESS(OK, "서비스에 가입한 친구 및 같은 그룹 친구 조회에 성공하였습니다."),
    SCHOOL_NAME_SEARCH_SCHOOL_SUCCESS(OK, "학교 검색에 성공하였습니다."),
    DEPARTMENT_NAME_SEARCH_BY_SCHOOL_NAME_SCHOOL_SUCCESS(OK, "학교 이름으로 학과 검색에 성공하였습니다."),
    REVEAL_NAME_HINT_SUCCESS(OK, "이름 초성 확인하는데 성공했습니다."),

    /**
     * 201 CREATED
     */
    SIGNUP_SUCCESS(CREATED, "회원가입이 완료됐습니다."),
    CREATE_BOARD_SUCCESS(CREATED, "게시물 생성이 완료됐습니다."),
    LOGIN_SUCCESS(CREATED, "로그인이 성공했습니다."),
    SIGN_UP_SUCCESS(CREATED, "회원 가입에 성공했습니다. 환영합니다."),
    CREATE_VOTE_SUCCESS(CREATED, "투표를 성공했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
