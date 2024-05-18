package com.camus.backend.chat.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.document.NoticeMessage;
import com.camus.backend.chat.domain.dto.FilteredMessageDto;
import com.camus.backend.chat.domain.repository.RedisChatRepository;
import com.camus.backend.chat.service.KafkaProducer.KafkaRedisChatProducer;
import com.camus.backend.chat.util.ChatNoticeType;

@Service
public class RedisChatService {
	private final RedisChatRepository redisChatRepository;
	private final KafkaRedisChatProducer kafkaRedisChatProducer;

	RedisChatService(RedisChatRepository redisChatRepository,
		KafkaRedisChatProducer kafkaRedisChatProducer) {
		this.redisChatRepository = redisChatRepository;
		this.kafkaRedisChatProducer = kafkaRedisChatProducer;
	}

	public void saveCommonMessageToRedis(
		CommonMessage commonMessage) {

		long messageId = redisChatRepository.addCommonMessage(commonMessage);
		commonMessage.setMessageId(messageId);

		// TODO : KafKa에 redis에 저장됐다 메시지 전송
		kafkaRedisChatProducer.sendCommonMessage(commonMessage);
	}

	public void saveFilteredMessageToRedis(
		FilteredMessageDto filteredMessageDto) {
		// WOO TODO : 필터링 저장 로직
		if (redisChatRepository.addFilteredType(filteredMessageDto)) {
			// WOO TODO : KafKa에 redis에 저장됐다 메시지 전송
			kafkaRedisChatProducer.sendFilterMessage(
				filteredMessageDto
			);
			System.out.println("kafka success");
		}

	}

	public void createChatRoomNotice(String roomId, UUID userId) {
		NoticeMessage firstNoticeMessage = NoticeMessage.builder()
			.roomId(UUID.fromString(roomId))
			.createdDate(LocalDateTime.now())
			.content(ChatNoticeType.ENTER_ROOM.getNoticeContent())
			.target(userId)
			.noticeType(ChatNoticeType.ENTER_ROOM.getNoticeType())
			.build();

		redisChatRepository.addNoticeMessage(firstNoticeMessage);
		redisChatRepository.createStreamConsumerGroup(roomId
			, userId);

		// TODO : KafKa에 redis에 저장됐다 메시지 전송
		kafkaRedisChatProducer.sendNoticeMessage(firstNoticeMessage);

	}

	public void newUserEnterRoomNotice(String roomId, UUID userId) {
		NoticeMessage newUserEnterRoomNotice = NoticeMessage.builder()
			.roomId(UUID.fromString(roomId))
			.createdDate(LocalDateTime.now())
			.content(ChatNoticeType.ENTER_ROOM.getNoticeContent())
			.target(userId)
			.noticeType(ChatNoticeType.ENTER_ROOM.getNoticeType())
			.build();

		//redis에 저장
		redisChatRepository.addNoticeMessage(newUserEnterRoomNotice);
		//사용자 - consumer에 추가
		redisChatRepository.updateStreamConsumerGroup(roomId
			, userId, redisChatRepository.getLatestRedisMessageId(roomId));

		kafkaRedisChatProducer.sendNoticeMessage(newUserEnterRoomNotice);
	}
}
