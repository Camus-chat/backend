package com.camus.backend.chat.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChatNoticeType {
	CREATE_ROOM("채팅이 시작되었습니다.", "create"),
	ENTER_ROOM("님이 채팅방에 입장하였습니다.", "enter"),
	LEAVE_ROOM("님이 채팅방을 나갔습니다.", "leave"),
	CLOSE_ROOM("채팅이 종료되었습니다.", "close");

	private final String noticeContent;
	private final String noticeType;

}
