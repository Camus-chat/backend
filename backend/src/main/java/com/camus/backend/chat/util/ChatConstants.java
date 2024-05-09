package com.camus.backend.chat.util;

public final class ChatConstants {
	public String REDIS_STREAM_COUNTER_KEY(String roomId) {
		return "chat:room:" + roomId + ":counter";
	}
}
