package com.camus.backend.chat.service.KafkaProducer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.document.NoticeMessage;
import com.camus.backend.chat.domain.document.RedisSavedCommonMessage;
import com.camus.backend.chat.domain.document.RedisSavedNoticeMessage;
import com.camus.backend.chat.domain.dto.FilteredMessageDto;
import com.camus.backend.chat.util.ChatModules;
import com.camus.backend.filter.domain.Request.SingleFilteringRequest;
import com.camus.backend.filter.util.component.FilterConstants;
import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaRedisChatProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ChatModules chatModules;
	private final FilterConstants filterConstants;
	private final ObjectMapper objectMapper;

	public KafkaRedisChatProducer(KafkaTemplate<String, Object> kafkaTemplate,
		ChatModules chatModules,
		FilterConstants filterConstants,
		ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.chatModules = chatModules;
		this.filterConstants = filterConstants;
		this.objectMapper = objectMapper;
	}

	public void sendNoticeMessage(NoticeMessage noticeMessage) {
		RedisSavedNoticeMessage redisSavedNoticeMessageDto = new RedisSavedNoticeMessage(noticeMessage);
		System.out.println("Redis에 Notice저장했음" + redisSavedNoticeMessageDto);
		kafkaTemplate.send(
			noticeMessage.getRoomId().toString(),
			redisSavedNoticeMessageDto
		);
	}

	// FEATUREID : 채팅 메시지를 저장한 후 Kafka에 요청하는 메서드
	public void sendCommonMessage(CommonMessage commonMessage) {
		RedisSavedCommonMessage redisSavedCommonMessage = new RedisSavedCommonMessage(commonMessage);
		System.out.println("Redis에 Common저장했음" + redisSavedCommonMessage);
		kafkaTemplate.send(
			chatModules.getRedisToClientRoomTopic(redisSavedCommonMessage.getRoomId()),
			redisSavedCommonMessage
		);

		this.sendSingleFilteringRequest(commonMessage);
	}

	// FEATUREID : AI 필터링 요청을 Kafka에 요청하는 메서드
	private void sendSingleFilteringRequest(CommonMessage commonMessage) {
		kafkaTemplate.send(
			filterConstants.FILTERING_REQ_TOPIC,
			new SingleFilteringRequest(
				commonMessage
			)
		);
		System.out.println("ai에 요청했음." + commonMessage);
	}

	// FEATUREID : 필터링된 메시지를 저장한 후 Kafka에 요청하는 메서드
	public void sendFilterMessage(FilteredMessageDto filteredMessageDto) {

		try {
			String filteredMessageToJson = objectMapper.writeValueAsString(filteredMessageDto);
			kafkaTemplate.send(
				chatModules.getRedisToClientRoomTopic(filteredMessageDto.getRoomId()),
				filteredMessageToJson
			);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}
	}
}


