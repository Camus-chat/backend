package com.camus.backend.chat.util;

import org.springframework.stereotype.Component;

@Component
public class ChatModules {
	public String getRedisCountKey(String roomId) {
		return "chat:room:" + roomId + ":counter";
	}
}
