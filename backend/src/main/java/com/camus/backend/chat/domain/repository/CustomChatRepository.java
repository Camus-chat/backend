package com.camus.backend.chat.domain.repository;

import java.util.List;
import java.util.UUID;

import com.camus.backend.chat.domain.document.Message;

public interface CustomChatRepository {

	public List<Message> getMessagesFromMongoByPage(UUID roomId, long startMessageId);
}
