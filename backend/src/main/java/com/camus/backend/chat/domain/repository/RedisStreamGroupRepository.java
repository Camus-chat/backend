package com.camus.backend.chat.domain.repository;

import java.util.List;
import java.util.UUID;

import com.camus.backend.chat.domain.dto.RedisSavedMessageBasicDto;

public interface RedisStreamGroupRepository {

	public int getUserUnreadMessageSize(String roomId, UUID userId);

	public List<RedisSavedMessageBasicDto> getMessagesFromRedis(String roomId,
		String startRedisMessageId);

	String getStreamConsumerAlreadyReadRedisMessageId(String roomId, UUID userId);

	String getLastestRedisMessageIdFromStream(String roomId);

}
