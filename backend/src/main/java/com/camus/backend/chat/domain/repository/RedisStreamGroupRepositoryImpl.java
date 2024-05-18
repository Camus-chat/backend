package com.camus.backend.chat.domain.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import com.camus.backend.chat.domain.document.RedisSavedCommonMessage;
import com.camus.backend.chat.domain.document.RedisSavedMessageBasic;
import com.camus.backend.chat.domain.document.RedisSavedNoticeMessage;
import com.camus.backend.chat.domain.dto.ChatDataListDto;
import com.camus.backend.chat.domain.dto.FilteredMessageDto;
import com.camus.backend.chat.domain.dto.PaginationDto;
import com.camus.backend.chat.domain.dto.chatmessagedto.CommonMessageDto;
import com.camus.backend.chat.domain.dto.chatmessagedto.MessageBasicDto;
import com.camus.backend.chat.domain.dto.chatmessagedto.NoticeMessageDto;
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
	public RedisSavedMessageBasic getLatestMessageFromStream(String roomId) {
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

	public List<RedisSavedMessageBasic> getMessagesFromRedisByEndId(String roomId, String endRedisId) {

		StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
		String streamKey = chatModules.getRedisStreamKey(roomId);
		Range<String> range = Range.rightOpen(endRedisId, "+");
		List<MapRecord<String, String, String>> messages = streamOps.range(streamKey, range,
			Limit.limit().count(chatConstants.CHAT_MESSAGE_PAGE_SIZE));

		if (messages.isEmpty()) {
			return null;
		}
		List<RedisSavedMessageBasic> result = new ArrayList<>();
		for (MapRecord<String, String, String> message : messages) {
			Map<String, String> valueMap = message.getValue();
			RedisSavedMessageBasic msg = convertToRedisSavedMessageBasicDto(valueMap);
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
			return new ChatDataListDto();
		}
		List<MessageBasicDto> result = new ArrayList<>();

		for (MapRecord<String, String, String> message : messages) {
			Map<String, String> valueMap = message.getValue();
			// FIXME : 두번 Convert 삭제
			RedisSavedMessageBasic msg = convertToRedisSavedMessageBasicDto(valueMap);

			result.add(convertToMessageBasicDto(msg));

		}

		return new ChatDataListDto(result, new PaginationDto(
			messages.get(messages.size() - 1).getId().toString(),
			result.size()));
	}

	private RedisSavedMessageBasic convertToRedisSavedMessageBasicDto(
		Map<String, String> valueMap) {
		String className = valueMap.get("_class");
		RedisSavedMessageBasic msg;
		switch (className) {
			case "NoticeMessage" -> msg = objectMapper.convertValue(valueMap, RedisSavedNoticeMessage.class);
			case "CommonMessage" -> msg = objectMapper.convertValue(valueMap, RedisSavedCommonMessage.class);
			default -> throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
		}
		return msg;
	}

	// FIXME : 중복 메서드 삭제
	private MessageBasicDto convertToMessageBasicDto(RedisSavedMessageBasic redisSavedMessageBasic) {
		String className = redisSavedMessageBasic.get_class();
		MessageBasicDto msg;
		switch (className) {
			case "NoticeMessage" -> msg = new NoticeMessageDto((RedisSavedNoticeMessage)redisSavedMessageBasic);
			case "CommonMessage" -> msg = this.addFilteredTypeToCommonMessage(
				new CommonMessageDto((RedisSavedCommonMessage)redisSavedMessageBasic),
				chatModules.getFilteredHashKeyByRoomId(redisSavedMessageBasic.getRoomId().toString()),
				chatModules.getFilteredZsetKeyByRoomId(redisSavedMessageBasic.getRoomId().toString())
			);
			default -> throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
		}
		return msg;
	}

	// FEATUREID : CommonMessage를 필터링 되도록 설정
	public MessageBasicDto addFilteredTypeToCommonMessage(
		CommonMessageDto commonMessageDto,
		String hashStreamKey,
		String zSetKey
	) {
		HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
		ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

		String timeString = hashOps.get(hashStreamKey, String.valueOf(commonMessageDto.getMessageId()));

		//필터링 내역 있을 때만
		if (timeString != null) {
			long time = Long.parseLong(timeString);
			Set<String> filterInfoFromSet = zSetOps.rangeByScore(zSetKey, time, time);
			// 데이터 불일치
			if (filterInfoFromSet == null || filterInfoFromSet.isEmpty()) {
				throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
			}

			// 동일한 시간 대에 입력된 값이 있다면
			FilteredMessageDto matchedDto = filterInfoFromSet.stream()
				.map(filterInfo -> objectMapper.convertValue(filterInfo, FilteredMessageDto.class))
				.filter(filteredMessageDto -> filteredMessageDto.getMessageId() == commonMessageDto.getMessageId())
				.findFirst()
				.orElse(null);

			if (matchedDto == null) {
				throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
			}

			// 필터 레벨 설정
			commonMessageDto.setFilteredLevel(matchedDto.getFilteredLevel());
		}

		return commonMessageDto;
	}

	public long getMessageIdByRedisId(String redisId, String roomId) {
		String streamKey = chatModules.getRedisStreamKey(roomId);
		StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
		List<MapRecord<String, String, String>> records = streamOps.range(
			streamKey,
			Range.closed(redisId, redisId),
			Limit.limit().count(1)
		);

		if (records != null && !records.isEmpty()) {
			convertToRedisSavedMessageBasicDto(records.get(0).getValue());
			return Long.parseLong(records.get(0).getValue().get("messageId"));
		}

		throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
	}

}
