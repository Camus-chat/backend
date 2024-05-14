package com.camus.backend.chat.domain.repository;

import java.util.List;

import com.camus.backend.chat.domain.document.Message;

public interface CustomChatRepository {
	public List<Message> getMessagesByPage(
		String roomId,
		long lastMessageId,
		long streamMessageCount,
		long mongoDBStartPageIndex,
		int page
	);
}
