package com.camus.backend.chat.domain.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Repository;

import com.camus.backend.chat.domain.dto.RedisSavedCommonMessageDto;
import com.camus.backend.chat.domain.dto.RedisSavedMessageBasicDto;
import com.camus.backend.chat.domain.dto.RedisSavedNoticeMessageDto;
import com.camus.backend.chat.util.ChatConstants;
import com.camus.backend.chat.util.ChatModules;
import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class RedisStreamGroupRepositoryImpl implements RedisStreamGroupRepository {

	private final RedisTemplate<String, String> redisTemplate;
	private final ChatModules chatModules;
	private final ChatConstants chatConstants;
	private final ObjectMapper objectMapper;

	public RedisStreamGroupRepositoryImpl(RedisTemplate<String, String> redisTemplate,
		ChatModules chatModules,
		ChatConstants chatConstants,
		ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		this.chatModules = chatModules;
		this.chatConstants = chatConstants;
		this.objectMapper = objectMapper;
	}

	public String getStreamConsumerAlreadyReadRedisMessageId(String roomId, UUID userId) {
		Object messageIdObj = redisTemplate.opsForHash().get(
			chatModules.getStreamConsumerGroupKey(roomId),
			chatModules.getStreamUserAlreadyReadRedisMessageIdKey(roomId, userId.toString())
		);

		if (messageIdObj == null) {
			throw new CustomException(ErrorCode.NOTFOUND_USER);
		}

		if (messageIdObj instanceof String) {
			return (String)messageIdObj;
		} else {
			System.out.println("Error : etStreamConsumerUnreadRedisMessageId");
			throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
		}
	}

	// FIXME : 사용자가 안 읽은 메시지 갯수 가져오기
	public int getUserUnreadMessageSize(String roomId, UUID userId) {

		// String streamKey = chatModules.getRedisStreamKey(roomId);
		//
		// RedisSavedMessageBasicDto latestMessage = getLatestMessageFromStream(roomId);
		//
		// String consumerReadMessageId = getStreamConsumerUnreadRedisMessageId(roomId, userId);
		//
		// long unreadMessageSize = latestMessage.getMessageId() - consumerReadMessageId;
		//
		// // 읽어올 메시지 갯수에 대한 설정 (안 읽은 메시지 사이즈에 대한 트레킹)
		// if (unreadMessageSize > chatConstants.CHAT_MESSAGE_PAGE_SIZE) {
		// 	updateStreamConsumerGroup(roomId, userId,
		// 		String.valueOf(latestMessage.getMessageId() + chatConstants.CHAT_MESSAGE_PAGE_SIZE - 1));
		// 	return chatConstants.CHAT_MESSAGE_PAGE_SIZE;
		// } else if (unreadMessageSize < 0) {
		// 	throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
		// } else {
		// 	// FIXME : 강제 형변환
		// 	return (int)unreadMessageSize;
		// }
		return 0;
	}

	// FEATUREID: 가장 마지막 메시지 가져오는 메소드
	public RedisSavedMessageBasicDto getLatestMessageFromStream(String roomId) {
		StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
		String streamKey = chatModules.getRedisStreamKey(roomId);
		Range<String> range = Range.open("-", "-");

		List<MapRecord<String, String, String>> messages = streamOps.range(streamKey, range,
			Limit.limit().count(1));

		if (messages.isEmpty()) {
			return null;
		}

		MapRecord<String, String, String> message = messages.get(0);

		Map<String, String> valueMap = message.getValue();
		String className = valueMap.get("_class");

		RedisSavedMessageBasicDto msg;
		switch (className) {
			case "NoticeMessage" -> msg = objectMapper.convertValue(valueMap, RedisSavedNoticeMessageDto.class);
			case "CommonMessage" -> msg = objectMapper.convertValue(valueMap, RedisSavedCommonMessageDto.class);
			default -> throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
		}
		return msg;

	}

	public String getLastestRedisMessageIdFromStream(String roomId) {
		StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
		String streamKey = chatModules.getRedisStreamKey(roomId);
		Range<String> range = Range.open("-", "-");

		List<MapRecord<String, String, String>> messages = streamOps.range(streamKey, range,
			Limit.limit().count(1));

		if (messages.isEmpty()) {
			return null;
		}

		MapRecord<String, String, String> message = messages.get(0);

		return message.getId().getValue();
	}

	public List<RedisSavedMessageBasicDto> getMessagesFromRedis(String roomId, String startRedisId) {

		StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
		String streamKey = chatModules.getRedisStreamKey(roomId);
		Range<String> range = Range.leftOpen("-", startRedisId);
		List<MapRecord<String, String, String>> messages = streamOps.range(streamKey, range,
			Limit.limit().count(chatConstants.CHAT_MESSAGE_PAGE_SIZE));

		if (messages.isEmpty()) {
			return null;
		}
		List<RedisSavedMessageBasicDto> result = new ArrayList<>();
		for (MapRecord<String, String, String> message : messages) {
			Map<String, String> valueMap = message.getValue();
			String className = valueMap.get("_class");
			RedisSavedMessageBasicDto msg;
			switch (className) {
				case "NoticeMessage" -> msg = objectMapper.convertValue(valueMap, RedisSavedNoticeMessageDto.class);
				case "CommonMessage" -> msg = objectMapper.convertValue(valueMap, RedisSavedCommonMessageDto.class);
				default -> throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
			}
			result.add(msg);
		}

		return result;
	}

}
