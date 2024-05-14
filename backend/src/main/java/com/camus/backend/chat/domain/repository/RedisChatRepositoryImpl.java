package com.camus.backend.chat.domain.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Repository;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.document.Message;
import com.camus.backend.chat.domain.document.NoticeMessage;
import com.camus.backend.chat.util.ChatConstants;
import com.camus.backend.chat.util.ChatModules;
import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;

@Repository
public class RedisChatRepositoryImpl implements RedisChatRepository {

	private RedisTemplate<String, String> redisTemplate;
	private ChatModules chatModules;
	private ChatConstants chatConstants;

	public RedisChatRepositoryImpl(RedisTemplate<String, String> redisTemplate,
		ChatModules chatModules,
		ChatConstants chatConstants) {
		this.redisTemplate = redisTemplate;
		this.chatModules = chatModules;
		this.chatConstants = chatConstants;
	}

	// 각 사용자의 Stream 별 읽은 메시지 위치를 기록하는 곳
	public void createStreamConsumerGroup(String roomId, UUID ownerUserId) {
		// 최초 사용자는 Owner / 자신의 참여 메시지를 읽은 것으로 처리
		redisTemplate.opsForHash().put(
			chatModules.getStreamConsumerGroupKey(roomId),
			chatModules.getStreamUserAlreadyReadRedisMessageIdKey(roomId, ownerUserId.toString()),
			getLatestRedisMessageId(roomId));
	}

	public void updateStreamConsumerGroup(String roomId, UUID userId, String streamMessageId) {
		redisTemplate.opsForHash().put(
			chatModules.getStreamConsumerGroupKey(roomId),
			userId.toString(),
			streamMessageId
		);
	}

	// STREAM의 내부 메시지 갯수에 대한 트레킹을 하는 Count Stream
	public String getStreamCount(UUID roomId) {
		String count = redisTemplate.opsForValue().get(chatModules.getRedisCountKey(roomId.toString()));
		return count != null ? count : "0";
	}

	public void updateStreamCount(UUID roomId) {
		redisTemplate.opsForValue().increment(chatModules.getRedisCountKey(roomId.toString()));
	}

	// Redis Stream에 메시지를 추가하는 메소드
	public void addMessageToStream(UUID roomId, Map<String, String> message) {
		MapRecord<String, String, String> record = StreamRecords.newRecord()
			.in(chatModules.getRedisStreamKey(roomId.toString()))
			.ofMap(message);
		redisTemplate.opsForStream().add(record);

		// 현재 스트림의 메시지 갯수 증가
		this.updateStreamCount(roomId);
	}

	public void addNoticeMessage(NoticeMessage noticeMessage) {
		addMessageToStream(noticeMessage.getRoomId(), Map.of(
			// 공통 필드
			Message.Fields.messageId, getStreamCount(noticeMessage.getRoomId()),
			Message.Fields.roomId, noticeMessage.getRoomId().toString(),
			Message.Fields.createdDate, noticeMessage.getCreatedDate().toString(),
			Message.Fields.content, noticeMessage.getContent(),
			// 상속 클래스의 특정 필드
			NoticeMessage.Fields.noticeType, noticeMessage.getNoticeType(),
			NoticeMessage.Fields.target, noticeMessage.getTarget().toString(),
			NoticeMessage.Fields._class, noticeMessage.get_class()
		));
	}

	public void addCommonMessage(CommonMessage commonMessage) {

		addMessageToStream(commonMessage.getRoomId(), Map.of(
			// 공통 필드
			Message.Fields.messageId, getStreamCount(commonMessage.getRoomId()),
			Message.Fields.roomId, commonMessage.getRoomId().toString(),
			Message.Fields.createdDate, commonMessage.getCreatedDate().toString(),
			Message.Fields.content, commonMessage.getContent(),
			// 상속 클래스의 특정 필드
			CommonMessage.Fields.senderId, commonMessage.getSenderId().toString(),
			CommonMessage.Fields.filteredType, commonMessage.getFilteredType(),
			CommonMessage.Fields.sentimentType, commonMessage.getSentimentType(),
			CommonMessage.Fields._class, commonMessage.get_class()
		));
	}

	//FIXME : 과연 사용할 필요가 있는가?
	public String getLatestRedisMessageId(String roomId) {

		String streamKey = chatModules.getRedisStreamKey(roomId.toString());
		StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();
		List<MapRecord<String, String, String>> records = streamOps.reverseRange(
			streamKey,
			Range.open("-", "+"),
			Limit.limit().count(1)
		);

		if (records != null && !records.isEmpty()) {
			System.out.println(records.get(0).getId().getValue());
			return records.get(0).getId().getValue();
		}

		throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
	}

}
