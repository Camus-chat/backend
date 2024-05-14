package com.camus.backend.chat.domain.repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
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

	public String getStreamCount(UUID roomId) {
		String count = redisTemplate.opsForValue().get(chatModules.getRedisCountKey(roomId.toString()));
		return count != null ? count : "0";
	}

	public void addMessageToStream(UUID roomId, Map<String, String> message) {
		MapRecord<String, String, String> record = StreamRecords.newRecord()
			.in(chatModules.getRedisStreamKey(roomId.toString()))
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

	public void setLastMessageId(UUID roomId, String messageId) {
		redisTemplate.opsForValue().set(chatModules.getRedisSavedLastMessageKey(roomId.toString()), messageId);
	}

	public String getLatestRedisMessageId(UUID roomId) {

		String streamKey = chatModules.getRedisStreamKey(roomId.toString());
		StreamOffset<String> offset = StreamOffset.fromStart(streamKey);
		Range<String> range = Range.open("-", "+");
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

	public List<Message> getRedisMessagesByPage(UUID roomId, int page, int userUnreadMessageSize) {
		if (page < 0) {
			return null;
		}

		StreamOperations<String, String, String> options = redisTemplate.opsForStream();
		int size = 0;

		if (page == 0) {
			size = chatConstants.CHAT_MESSAGE_PAGE_SIZE - userUnreadMessageSize;
			// TODO : 특정 메시지부터 읽어오기 > Redis에서 부여하는 Id 값을 찾아야 해서 보류
		}
		StreamReadOptions readOptions = StreamReadOptions.empty().count(size).block(Duration.ZERO);

		return null;
	}
}
