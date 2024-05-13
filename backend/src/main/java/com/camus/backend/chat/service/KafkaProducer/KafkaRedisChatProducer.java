package com.camus.backend.chat.service.KafkaProducer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.NoticeMessage;
import com.camus.backend.chat.domain.dto.RedisSavedNoticeMessageDto;

@Service
public class KafkaRedisChatProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public KafkaRedisChatProducer(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendNoticeMessage(NoticeMessage noticeMessage) {

		RedisSavedNoticeMessageDto redisSavedNoticeMessageDto = new RedisSavedNoticeMessageDto(noticeMessage);
		System.out.println(redisSavedNoticeMessageDto);
		kafkaTemplate.send(
			noticeMessage.getRoomId().toString(),
			redisSavedNoticeMessageDto
		);
	}

}
