package com.camus.backend.chat.domain.repository;

import java.util.List;
import java.util.UUID;

import com.camus.backend.chat.util.ChatConstants;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.camus.backend.chat.domain.document.Message;
import com.camus.backend.chat.util.ChatModules;


@Repository
public class CustomChatRepositoryImpl implements CustomChatRepository {
	// TODO : MONGO TEMPLATE 사용

	private final MongoTemplate mongoTemplate;
	private final ChatModules chatModules;
	private final ChatConstants chatConstants;

	public CustomChatRepositoryImpl(MongoTemplate mongoTemplate,
		ChatModules chatModules,
									ChatConstants chatConstants) {
		this.mongoTemplate = mongoTemplate;
		this.chatModules = chatModules;
		this.chatConstants = chatConstants;
	}

	public List<Message> getMessagesFromMongoByPage(UUID roomId, long startMessageId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("roomId").is(roomId).and("messageId").lt(startMessageId));
		query.with(Sort.by(Sort.Order.desc("messageId")));
		query.limit(chatConstants.CHAT_MESSAGE_PAGE_SIZE);
		return mongoTemplate.find(query, Message.class);
	}

}
