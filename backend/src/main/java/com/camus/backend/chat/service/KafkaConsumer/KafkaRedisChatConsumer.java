package com.camus.backend.chat.service.KafkaConsumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.message.StompToRedisMessage;
import com.camus.backend.chat.service.RedisChatService;
import com.camus.backend.chat.util.ChatConstants;
import com.camus.backend.chat.util.ChatModules;
import com.camus.backend.filter.util.type.FilteredType;

@Service
public class KafkaRedisChatConsumer {

	private final ConcurrentKafkaListenerContainerFactory<String, String> factory;
	private final ChatModules chatModules;
	private final ChatConstants chatConstants;

	private final RedisChatService redisChatService;

	public KafkaRedisChatConsumer(ConcurrentKafkaListenerContainerFactory<String, String> factory,
		ChatModules chatModules,
		ChatConstants chatConstants,
		RedisChatService redisChatService) {
		this.factory = factory;
		this.chatModules = chatModules;
		this.chatConstants = chatConstants;
		this.redisChatService = redisChatService;
	}

	@KafkaListener(topics = chatConstants.REDIS_LISTEN_TOPIC)
	public void listenToSaveRedis(StompToRedisMessage kafkaMessage) {
		CommonMessage commonMessage = CommonMessage.builder()
			.content(kafkaMessage.getContent())
			.roomId(UUID.fromString(kafkaMessage.getRoomId()))
			.senderId(UUID.fromString(kafkaMessage.getUserId()))
			.filteredType(FilteredType.NOT_FILTERED.toString())
			.build();

		redisChatService.saveCommonMessageToRedis(commonMessage);
	}
}
