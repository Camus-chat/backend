package com.camus.backend.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.Message;
import com.camus.backend.chat.domain.repository.MongoChatRepository;
import com.camus.backend.chat.domain.repository.RedisChatRepository;
import com.camus.backend.chat.domain.repository.RedisStreamGroupRepository;

@Service
public class ChatDataService {
	private final RedisChatRepository redisChatRepository;
	private final MongoChatRepository mongoChatRepository;

	private final RedisStreamGroupRepository redisStreamGroupRepository;

	ChatDataService(RedisChatRepository redisChatRepository, MongoChatRepository mongoChatRepository,
		RedisStreamGroupRepository redisStreamGroupRepository) {
		this.redisChatRepository = redisChatRepository;
		this.mongoChatRepository = mongoChatRepository;
		this.redisStreamGroupRepository = redisStreamGroupRepository;
	}

	public String getLastMessageIdOfRedis(
		UUID roomId
	) {
		return redisChatRepository.getLatestRedisMessageId(
			roomId
		);
	}

	public long getStreamCountOfRedis(
		UUID roomId
	) {
		return Long.parseLong(redisChatRepository.getStreamCount(
			roomId
		));
	}

	public int getUserUnreadMessageSize(
		UUID roomId,
		UUID userId
	) {
		return redisStreamGroupRepository.getUserUnreadMessageSize(
			roomId.toString(),
			userId
		);
	}

	public List<Message> getMessageFromRedis(
		UUID roomId,
		int page
	) {

		return new ArrayList<>();
	}

	public List<Message> getMessageFromMongo(
		UUID roomId,
		long lastMessageId,
		long streamMessageCount,
		long mongoDBStartPageIndex,
		int page
	) {
		return mongoChatRepository.getMessagesByPage(
			roomId.toString(),
			lastMessageId,
			streamMessageCount,
			mongoDBStartPageIndex,
			page
		);
	}

}
