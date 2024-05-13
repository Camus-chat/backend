package com.camus.backend.chat.domain.repository;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.camus.backend.chat.domain.document.Message;
import com.camus.backend.chat.util.ChatModules;

@Repository
public class CustomChatRepositoryImpl implements CustomChatRepository {
	// TODO : MONGO TEMPLATE 사용

	private final MongoTemplate mongoTemplate;
	private final ChatModules chatModules;

	public CustomChatRepositoryImpl(MongoTemplate mongoTemplate,
		ChatModules chatModules) {
		this.mongoTemplate = mongoTemplate;
		this.chatModules = chatModules;
	}

	public List<Message> getMessagesByPage(
		String roomId,
		long lastMessageId,
		long streamMessageCount,
		long mongoDBStartPageIndex,
		int page
	) {

		return null;
	}

}
