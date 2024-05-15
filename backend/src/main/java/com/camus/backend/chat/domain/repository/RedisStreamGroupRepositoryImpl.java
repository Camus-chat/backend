package com.camus.backend.chat.domain.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.camus.backend.chat.domain.dto.*;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Repository;

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

	// 이전에 불러와서 쏴준 내용을 기반으로 메시지 읽은 위치 기록
	public void updateStreamConsumerAlreadyReadRedisMessageId(String roomId, UUID userId, String latestRedisMessageId) {
		redisTemplate.opsForHash().put(
			chatModules.getStreamConsumerGroupKey(roomId),
			chatModules.getStreamUserAlreadyReadRedisMessageIdKey(roomId, userId.toString()),
			latestRedisMessageId
		);
	}


	// FEATUREID: 가장 마지막 메시지 가져오는 메소드
	public RedisSavedMessageBasicDto getLatestMessageFromStream(String roomId) {
		StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
		String streamKey = chatModules.getRedisStreamKey(roomId);
		Range<String> range = Range.open("-", "+");

		List<MapRecord<String, String, String>> messages = streamOps.reverseRange(streamKey, range,
			Limit.limit().count(1));

		if (messages.isEmpty()) {
			return null;
		}

		MapRecord<String, String, String> message = messages.get(0);

		Map<String, String> valueMap = message.getValue();

		return convertToRedisSavedMessageBasicDto(valueMap);

	}

	public String getLatestRedisMessageIdFromStream(String roomId) {
		StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
		String streamKey = chatModules.getRedisStreamKey(roomId);
		Range<String> range = Range.open("-", "+");

		List<MapRecord<String, String, String>> messages = streamOps.reverseRange(streamKey, range,
			Limit.limit().count(1));

		if (messages.isEmpty()) {
			return null;
		}

		MapRecord<String, String, String> message = messages.get(0);

		return message.getId().getValue();
	}

	public List<RedisSavedMessageBasicDto> getMessagesFromRedisByEndId(String roomId, String endRedisId) {

		StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
		String streamKey = chatModules.getRedisStreamKey(roomId);
		Range<String> range = Range.rightOpen(endRedisId, "+");
		List<MapRecord<String, String, String>> messages = streamOps.range(streamKey, range,
			Limit.limit().count(chatConstants.CHAT_MESSAGE_PAGE_SIZE));

		if (messages.isEmpty()) {
			return null;
		}
		List<RedisSavedMessageBasicDto> result = new ArrayList<>();
		for (MapRecord<String, String, String> message : messages) {
			Map<String, String> valueMap = message.getValue();
			RedisSavedMessageBasicDto msg = convertToRedisSavedMessageBasicDto(valueMap);
			result.add(msg);
		}

		return result;
	}

	// pagination을 위한 nextMessageId기록 포함 DTo반환
	public ChatDataListDto getMessagesFromRedisByStartId(String roomId, String startRedisId) {
		StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
		String streamKey = chatModules.getRedisStreamKey(roomId);
		Range<String> range = Range.leftOpen("-", startRedisId);
		List<MapRecord<String, String, String>> messages = streamOps.range(streamKey, range,
			Limit.limit().count(chatConstants.CHAT_MESSAGE_PAGE_SIZE));

		if (messages.isEmpty()) {
			return new ChatDataListDto(			);
		}
		List<RedisSavedMessageBasicDto> result = new ArrayList<>();
		for (MapRecord<String, String, String> message : messages) {
			Map<String, String> valueMap = message.getValue();
			RedisSavedMessageBasicDto msg = convertToRedisSavedMessageBasicDto(valueMap);
			result.add(msg);
		}

		return new ChatDataListDto(result, new PaginationDto(
				messages.get(messages.size()-1).getId().toString(),
				result.size()));
	}

	private RedisSavedMessageBasicDto convertToRedisSavedMessageBasicDto(Map<String, String> valueMap) {
		String className = valueMap.get("_class");
		RedisSavedMessageBasicDto msg;
		switch (className) {
			case "NoticeMessage" -> msg = objectMapper.convertValue(valueMap, RedisSavedNoticeMessageDto.class);
			case "CommonMessage" -> msg = objectMapper.convertValue(valueMap, RedisSavedCommonMessageDto.class);
			default -> throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
		}
		return msg;
	}


}
