package com.camus.backend.chat.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.NoticeMessage;
import com.camus.backend.chat.domain.repository.RedisChatRepository;
import com.camus.backend.chat.util.ChatNoticeType;

@Service
public class RedisChatService {
	private final RedisChatRepository redisChatRepository;
	private final KafkaProducerService kafkaProducerService;

	RedisChatService(RedisChatRepository redisChatRepository,
		KafkaProducerService kafkaProducerService) {
		this.redisChatRepository = redisChatRepository;
		this.kafkaProducerService = kafkaProducerService;
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

		// TODO : KafKa에 redis에 저장됐다 메시지 전송
		// kafkaProducerService.sendMessage( , roomId);

	}
}
