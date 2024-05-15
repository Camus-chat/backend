package com.camus.backend.chat.util;

import org.springframework.stereotype.Component;

@Component
public class ChatModules {
	public String getRedisCountKey(String roomId) {
		return "chat:room:" + roomId + ":counter";
	}

	public String getRedisStreamKey(String roomId) {
		return "chat:room:" + roomId + ":stream";
	}

	public String getRedisSavedLastMessageKey(String roomId) {
		return "chat:room:" + roomId + ":lastMessage";
	}

	public String getStreamConsumerGroupKey(String roomId) {
		return "chat:room:" + roomId + ":group";
	}

	public String getStreamUserAlreadyReadRedisMessageIdKey(String roomId, String userId) {
		return "chat:room:" + roomId + ":user:" + userId + ":read";
	}
	public String getMongoDbMessageKey(String messageId) {
		return "DB"+messageId;
	}

	public long getMogoDBStartPageIndex(
		long lastMessageId,
		long streamMessageCount
	) {
		return (lastMessageId - streamMessageCount) / 300;
	}
}
