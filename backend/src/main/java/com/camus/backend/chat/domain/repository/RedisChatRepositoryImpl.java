package com.camus.backend.chat.domain.repository;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.document.Message;
import com.camus.backend.chat.domain.document.NoticeMessage;
import com.camus.backend.chat.util.ChatModules;

@Repository
public class RedisChatRepositoryImpl implements RedisChatRepository {

	private RedisTemplate<String, String> redisTemplate;
	private ChatModules chatModules;

	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate,
		ChatModules chatModules) {
		this.redisTemplate = redisTemplate;
		this.chatModules = chatModules;
	}

	public String getStreamCount(UUID roomId) {
		String count = redisTemplate.opsForValue().get(chatModules.getRedisCountKey(roomId.toString()));
		return count != null ? count : "0";
	}

	public void addMessageToStream(UUID roomId, Map<String, String> message) {
		MapRecord<String, String, String> record = StreamRecords.newRecord()
			.in(roomId.toString())
			.ofMap(message);
		redisTemplate.opsForStream().add(record);

		redisTemplate.opsForValue().increment(
			chatModules.getRedisCountKey(roomId.toString())
		);
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

}
