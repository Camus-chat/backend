package com.camus.backend.chat.service.KafkaConsumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.dto.FilteredMessageDto;
import com.camus.backend.chat.domain.message.StompToRedisMessage;
import com.camus.backend.chat.service.RedisChatService;
import com.camus.backend.chat.util.FilterMatch;
import com.camus.backend.filter.domain.Response.SingleFilteringResponse;
import com.camus.backend.filter.util.type.FilteredType;

@Service
public class KafkaRedisChatConsumer {

	private final ConcurrentKafkaListenerContainerFactory<String, String> factory;

	private final RedisChatService redisChatService;
	private final FilterMatch filterMatch;

	public KafkaRedisChatConsumer(ConcurrentKafkaListenerContainerFactory<String, String> factory,
		RedisChatService redisChatService,
		FilterMatch filterMatch) {
		this.factory = factory;
		this.redisChatService = redisChatService;
		this.filterMatch = filterMatch;
	}

	// Kafka에서 메시지를 받아와 Redis에 저장하는 메소드
	@KafkaListener(topics = "clientMessage", groupId = "REDIS_GROUP_ID")
	public void listenToSaveRedis(StompToRedisMessage kafkaMessage) {
		CommonMessage commonMessage = CommonMessage.builder()
			.content(kafkaMessage.getContent())
			.roomId(UUID.fromString(kafkaMessage.getRoomId()))
			.senderId(UUID.fromString(kafkaMessage.getUserId()))
			.filteredType(FilteredType.NOT_FILTERED.toString())
			.build();

		redisChatService.saveCommonMessageToRedis(commonMessage);
	}

	// Kafka에서 필터링 메시지를 받아와ㅓ Redis에 저장하는 메소드
	@KafkaListener(topics = "SingleFilteringResponse", groupId = "REDIS_FILTERED_GROUP_ID")
	public void listenToSaveFilteredRedis(SingleFilteringResponse singleFilteringResponse) {

		// 객체 만들어주기
		FilteredMessageDto filteredMessageDto = new FilteredMessageDto();
		filteredMessageDto.setRoomId(singleFilteringResponse.getRoomId());
		filteredMessageDto.setMessageId(singleFilteringResponse.getFilteredMessage().getId());
		filteredMessageDto.setFilteredLevel(
			filterMatch.FILTER_MAP.get(singleFilteringResponse.getFilteredMessage().getFilteredType().getValue())
				.toString());
		filteredMessageDto.setFilteredType(singleFilteringResponse.getFilteredMessage().getFilteredType().getValue());

		redisChatService.saveFilteredMessageToRedis(filteredMessageDto);
	}
}
