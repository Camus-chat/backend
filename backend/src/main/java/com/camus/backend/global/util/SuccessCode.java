package com.camus.backend.global.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {

	// MEMBER
	LOGOUT("MEMBER", "로그아웃 성공"),
	PROFILE_EDIT("MEMBER", "프로필 사진 변경 완료"),
	NICKNAME_EDIT("MEMBER", "닉네임 변경 완료"),

	// CHANEL
	CHANNEL_DISABLE("CHANEL", "채널 링크 비활성화 성공"),
	CHANNEL_EDIT("CHANEL", "채널명 변경 성공"),

	// ROOM
	ROOM_DISABLE("ROOM", "채팅방 닫기 성공"),

	;

	private final String okResponse = "OK";
	private final String feature;
	private final String successMessage;

}
