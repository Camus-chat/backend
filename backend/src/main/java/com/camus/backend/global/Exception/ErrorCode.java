package com.camus.backend.global.Exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
	// NOTFOUND
	NOTFOUND_USER(HttpStatus.NOT_FOUND, 401, "사용자가 존재하지 않습니다."),
	NOTFOUND_CHANNEL(HttpStatus.NOT_FOUND, 402, "채널이 존재하지 않습니다."),
	NOTFOUND_ROOM(HttpStatus.NOT_FOUND, 403, "채팅방이 존재하지 않습니다."),

	// CONFLICT
	CONFLICT_ID(HttpStatus.CONFLICT, 901, "이미 사용중인 아이디입니다."),

	// MISSING_PARAMETER
	MISSING_PARAMETER_ID(HttpStatus.BAD_REQUEST, 1001, "아이디를 입력해주세요."),
	MISSING_PARAMETER_PW(HttpStatus.BAD_REQUEST, 1002, "비밀번호를 입력해주세요."),

	// INVALID_PARAMETER
	INVALID_PARAMETER_EMAIL(HttpStatus.BAD_REQUEST, 1003, "올바르지 않은 이메일 형식입니다."),
	INVALID_PARAMETER_PW(HttpStatus.BAD_REQUEST, 1004, "올바르지 않은 비밀번호 형식입니다."),
	INVALID_PARAMETER_ID(HttpStatus.BAD_REQUEST, 1005, "올바르지 않은 아이디 형식입니다."),
	INVALID_PARAMETER_IMAGE(HttpStatus.BAD_REQUEST, 1006, "올바르지 않은 이미지 형식입니다."),
	INVALID_PARAMETER_NICKNAME(HttpStatus.BAD_REQUEST, 1007, "올바르지 않은 닉네임 형식입니다."),

	// BAD_REQUEST
	BAD_REQUEST_CHANNEL(HttpStatus.BAD_REQUEST, 1008, "이미 비활성화된 채널입니다."),

	// UNAUTHORIZED
	UNAUTHORIZED_PW(HttpStatus.UNAUTHORIZED, 102, "비밀번호가 일치하지 않습니다."),
	UNAUTHORIZED_ID(HttpStatus.UNAUTHORIZED, 103, "아이디가 일치하지 않습니다."),

	// FORBIDDEN
	FORBIDDEN_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, 301, "만료된 토큰입니다."),
	FORBIDDEN_ACCESS_DENIED(HttpStatus.FORBIDDEN, 302, "권한이 없습니다.");

	private final HttpStatus httpStatusCode;
	private final Integer errorKey;
	private final String errorMessage;

}
