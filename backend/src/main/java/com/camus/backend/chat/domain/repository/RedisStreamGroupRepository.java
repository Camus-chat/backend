package com.camus.backend.chat.domain.repository;

import java.util.List;
import java.util.UUID;

import com.camus.backend.chat.domain.document.RedisSavedMessageBasic;
import com.camus.backend.chat.domain.dto.ChatDataListDto;
import com.camus.backend.chat.domain.dto.chatmessagedto.CommonMessageDto;
import com.camus.backend.chat.domain.dto.chatmessagedto.MessageBasicDto;

public interface RedisStreamGroupRepository {
	public List<RedisSavedMessageBasic> getMessagesFromRedisByEndId(String roomId,
		String startRedisMessageId);

	public void updateStreamConsumerAlreadyReadRedisMessageId(String roomId, UUID userId,
		String latestMessageId);

	String getStreamConsumerAlreadyReadRedisMessageId(String roomId, UUID userId);

	ChatDataListDto getMessagesFromRedisByStartId(String roomId, String startRedisId);

	String getLatestRedisMessageIdFromStream(String roomId);

	RedisSavedMessageBasic getLatestMessageFromStream(String roomId);

	long getMessageIdByRedisId(String redisId, String roomId);

	MessageBasicDto addFilteredTypeToCommonMessage(
		CommonMessageDto commonMessageDto,
		String hashStreamKey,
		String zSetKey
	);
}
