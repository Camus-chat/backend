package com.camus.backend.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.Message;
import com.camus.backend.chat.domain.dto.RedisSavedMessageBasicDto;
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

	public List<RedisSavedMessageBasicDto> getUserUnreadMessage(
		UUID roomId,
		UUID userId
	) {

		String startReadRedisMessageId = redisStreamGroupRepository.getStreamConsumerAlreadyReadRedisMessageId(
			roomId.toString(),
			userId
		);
		List<RedisSavedMessageBasicDto> messages = new ArrayList<>();
		if (redisStreamGroupRepository.getLastestRedisMessageIdFromStream(roomId.toString())
			.equals(startReadRedisMessageId)) {
			return messages;
		}

		messages = redisStreamGroupRepository.getMessagesFromRedis(
			roomId.toString(),
			startReadRedisMessageId
		);
		return messages;
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
