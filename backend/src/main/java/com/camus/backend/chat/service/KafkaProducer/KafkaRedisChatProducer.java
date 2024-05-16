package com.camus.backend.chat.service.KafkaProducer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.document.NoticeMessage;
import com.camus.backend.chat.domain.dto.RedisSavedCommonMessageDto;
import com.camus.backend.chat.domain.dto.RedisSavedNoticeMessageDto;
import com.camus.backend.chat.util.ChatModules;
import com.camus.backend.filter.domain.Request.SingleFilteringRequest;
import com.camus.backend.filter.util.component.FilterConstants;

@Service
public class KafkaRedisChatProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ChatModules chatModules;
	private final FilterConstants filterConstants;

	public KafkaRedisChatProducer(KafkaTemplate<String, Object> kafkaTemplate,
		ChatModules chatModules,
		FilterConstants filterConstants) {
		this.kafkaTemplate = kafkaTemplate;
		this.chatModules = chatModules;
		this.filterConstants = filterConstants;
	}

	public void sendNoticeMessage(NoticeMessage noticeMessage) {
		RedisSavedNoticeMessageDto redisSavedNoticeMessageDto = new RedisSavedNoticeMessageDto(noticeMessage);
		System.out.println("Redis에 Notice저장했음" + redisSavedNoticeMessageDto);
		kafkaTemplate.send(
			noticeMessage.getRoomId().toString(),
			redisSavedNoticeMessageDto
		);
	}

	// FEATUREID : 채팅 메시지를 저장한 후 Kafka에 요청하는 메서드
	public void sendCommonMessage(CommonMessage commonMessage) {
		RedisSavedCommonMessageDto redisSavedCommonMessageDto = new RedisSavedCommonMessageDto(commonMessage);
		System.out.println("Redis에 Common저장했음" + redisSavedCommonMessageDto);
		kafkaTemplate.send(
			chatModules.getRedisToClientRoomTopic(redisSavedCommonMessageDto.getRoomId()),
			redisSavedCommonMessageDto
		);
	}

	private void sendSingleFilteringRequest(CommonMessage commonMessage) {
		System.out.println("ai에 요청했음." + commonMessage);
		kafkaTemplate.send(
			filterConstants.FILTERING_REQ_TOPIC,
			new SingleFilteringRequest(
				commonMessage
			)

		);
	}
}


