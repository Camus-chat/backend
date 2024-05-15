package com.camus.backend.chat.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.camus.backend.chat.domain.document.Message;

public interface MongoChatRepository extends MongoRepository<Message, String>, CustomChatRepository {
	// TODO : MONGO REPOSITORY사용

}
