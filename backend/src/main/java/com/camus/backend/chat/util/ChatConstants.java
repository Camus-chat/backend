package com.camus.backend.chat.util;

import org.springframework.stereotype.Component;

@Component
public final class ChatConstants {
	public int CHAT_MESSAGE_PAGE_SIZE = 300;

	public String REDIS_STREAM_COUNTER_KEY(String roomId) {
		return "chat:room:" + roomId + ":counter";
	}
}
