package com.camus.backend.chat.domain.repository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Repository;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.document.Message;
import com.camus.backend.chat.domain.document.NoticeMessage;
import com.camus.backend.chat.util.ChatModules;
import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class RedisStreamGroupRepositoryImpl implements RedisStreamGroupRepository {

	private RedisTemplate<String, String> redisTemplate;
	private ChatModules chatModules;
	private ObjectMapper objectMapper = new ObjectMapper();

	public RedisStreamGroupRepositoryImpl(RedisTemplate<String, String> redisTemplate,
		ChatModules chatModules) {
		this.redisTemplate = redisTemplate;
		this.chatModules = chatModules;
	}

	public void createStreamConsumerGroup(String roomId, UUID userId) {
		redisTemplate.opsForHash().put(
			chatModules.getStreamConsumerGroupKey(roomId),
			userId.toString(),
			"0"
		);
	}

	public void updateStreamConsumerGroup(String roomId, UUID userId, String streamMessageId) {
		redisTemplate.opsForHash().put(
			chatModules.getStreamConsumerGroupKey(roomId),
			userId.toString(),
			streamMessageId
		);
	}

	public long getStreamConsumerGroupLastMessageId(String roomId, UUID userId) {
		String messageId = (String)redisTemplate.opsForHash().get(
			chatModules.getStreamConsumerGroupKey(roomId),
			userId.toString()
		);

		return messageId != null ? Long.parseLong(messageId) : -1;
	}

	// FIXME : 사용자가 안 읽은 메시지 갯수 가져오기
	public int getUserUnreadMessageSize(String roomId, UUID userId) {

		String streamKey = chatModules.getRedisStreamKey(roomId);

		Message latestMessage = getLatestMessageFromStream(roomId);

		long consumerReadMessageId = getStreamConsumerGroupLastMessageId(roomId, userId);

		long unreadMessageSize = latestMessage.getMessageId() - consumerReadMessageId;

		// FIXME: 한 페이지 한계와 동일한 숫자로
		int maxUnreadMessageSize = 300;
		if (unreadMessageSize > maxUnreadMessageSize) {
			// 300개 이상일 경우 최대 300개까지만 읽도록 설정
			updateStreamConsumerGroup(roomId, userId,
				String.valueOf(latestMessage.getMessageId() + maxUnreadMessageSize - 1));
			return maxUnreadMessageSize;
		} else if (unreadMessageSize < 0) {
			throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
		} else {
			// FIXME : 강제 형변환
			return (int)unreadMessageSize;
		}

	}

	// FEATUREID: 가장 마지막 메시지 가져오는 메소드
	public Message getLatestMessageFromStream(String roomId) {
		StreamOperations<String, String, ObjectRecord<String, String>> streamOps = redisTemplate.opsForStream();

		Range<String> range = Range.open("-", "+");
		List<ObjectRecord<String, String>> messages = streamOps.reverseRange(
			String.class,
			chatModules.getRedisStreamKey(roomId),
			range,
			Limit.limit().count(1)
		);

		if (messages.isEmpty()) {
			return null;
		}

		ObjectRecord<String, String> message = messages.get(0);
		String jsonData = message.getValue();

		try {
			JsonNode rootNode = objectMapper.readTree(jsonData);
			String className = rootNode.path("_class").asText();

			return switch (className) {
				case "NoticeMessage" -> objectMapper.convertValue(rootNode, NoticeMessage.class);
				case "CommonMessage" -> objectMapper.convertValue(rootNode, CommonMessage.class);
				default -> throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
			};
		} catch (IOException e) {
			throw new CustomException(ErrorCode.DB_OPERATION_FAILED);
		}

	}

}
