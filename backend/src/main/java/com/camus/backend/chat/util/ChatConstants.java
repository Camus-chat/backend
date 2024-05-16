package com.camus.backend.chat.util;

import org.springframework.stereotype.Component;

@Component
public final class ChatConstants {
	public static final String REDIS_LISTEN_TOPIC = "CLIENT_TO_REDIS_TOPIC";
	public final int CHAT_MESSAGE_PAGE_SIZE = 300;

}
