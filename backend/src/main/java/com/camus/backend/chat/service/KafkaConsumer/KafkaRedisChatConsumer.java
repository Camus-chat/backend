package com.camus.backend.chat.service.KafkaConsumer;

import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.dto.FilteredMessageDto;
import com.camus.backend.chat.domain.message.StompToRedisMessage;
import com.camus.backend.chat.service.RedisChatService;
import com.camus.backend.chat.util.FilterMatch;
import com.camus.backend.filter.domain.Request.FilteringRequest;
import com.camus.backend.filter.domain.Response.ContextFilteringResponse;
import com.camus.backend.filter.domain.Response.FilteredMessage;
import com.camus.backend.filter.domain.Response.SingleFilteringResponse;
import com.camus.backend.filter.util.type.FilteredType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaRedisChatConsumer {

	private final ConcurrentKafkaListenerContainerFactory<String, String> factory;

	private final RedisChatService redisChatService;
	private final FilterMatch filterMatch;
	private final ObjectMapper objectMapper;

	public KafkaRedisChatConsumer(
		ConcurrentKafkaListenerContainerFactory<String, String> factory,
		RedisChatService redisChatService,
		FilterMatch filterMatch,
		ObjectMapper objectMapper
	) {
		this.factory = factory;
		this.redisChatService = redisChatService;
		this.filterMatch = filterMatch;
		this.objectMapper = objectMapper;
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
	@KafkaListener(topics = "SingleFilteringResponse", groupId = "REDIS_SINGLE_FILTERED_GROUP_ID")
	public void listenToSaveSingleFilteredRedis(ConsumerRecord<String, Object> record) {
		try {
			// 객체 만들어주기
			SingleFilteringResponse response = objectMapper.readValue(record.value().toString(), SingleFilteringResponse.class);
			if (response.getFilteredMessage().getFilteredType().equals(FilteredType.NOT_FILTERED)) return;
			FilteredMessageDto filteredMessageDto = new FilteredMessageDto();
			filteredMessageDto.setRoomId(response.getRoomId());
			filteredMessageDto.setMessageId(response.getFilteredMessage().getId());
			filteredMessageDto.setFilteredLevel(
				filterMatch.FILTER_MAP.get(response.getFilteredMessage().getFilteredType().getValue())
					.toString());
			filteredMessageDto.setFilteredType(response.getFilteredMessage().getFilteredType().getValue());
			filteredMessageDto.setCreatedDate(response.getFilteredMessage().getCreatedDate());
			redisChatService.saveFilteredMessageToRedis(filteredMessageDto);
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	@KafkaListener(topics = "ContextFilteringResponse", groupId = "REDIS_CONTEXT_FILTERED_GROUP_ID")
	public void listenToSaveContextFilteredRedis(ConsumerRecord<String, Object> record){
		try {
			ContextFilteringResponse response = objectMapper.readValue(record.value().toString(), ContextFilteringResponse.class);

			for (int i=0; i<response.getFilteredMessages().size(); i++){

				FilteredMessageDto filteredMessageDto = new FilteredMessageDto();
				FilteredMessage filteredMessage = response.getFilteredMessages().get(i);
				if (filteredMessage.getFilteredType().equals(FilteredType.NOT_FILTERED)) continue;

				filteredMessageDto.setRoomId(response.getRoomId());
				filteredMessageDto.setMessageId(filteredMessage.getId());
				filteredMessageDto.setFilteredLevel(
					filterMatch.FILTER_MAP.get(filteredMessage.getFilteredType().getValue())
						.toString());
				filteredMessageDto.setFilteredType(filteredMessage.getFilteredType().getValue());
				filteredMessageDto.setCreatedDate(filteredMessage.getCreatedDate());

				redisChatService.saveFilteredMessageToRedis(filteredMessageDto);
			}


		} catch (Exception e){
			e.printStackTrace();
		}

	}
}
