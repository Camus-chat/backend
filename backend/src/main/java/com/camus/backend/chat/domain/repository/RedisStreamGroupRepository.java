package com.camus.backend.chat.domain.repository;

import java.util.List;
import java.util.UUID;

import com.camus.backend.chat.domain.dto.ChatDataListDto;
import com.camus.backend.chat.domain.dto.RedisSavedMessageBasicDto;

public interface RedisStreamGroupRepository {
	public List<RedisSavedMessageBasicDto> getMessagesFromRedisByEndId(String roomId,
																	   String startRedisMessageId);

	public void updateStreamConsumerAlreadyReadRedisMessageId(String roomId, UUID userId,
		String latestMessageId);

	String getStreamConsumerAlreadyReadRedisMessageId(String roomId, UUID userId);
	ChatDataListDto getMessagesFromRedisByStartId(String roomId, String startRedisId);
	String getLatestRedisMessageIdFromStream(String roomId);

}
