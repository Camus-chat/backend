package com.camus.backend.chat.service.KafkaProducer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.message.StompToRedisMessage;
import com.camus.backend.chat.util.ChatConstants;

@Service
public class KafkaStompProducerService {
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public KafkaStompProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendMessage(
		StompToRedisMessage clientMessage
	) {
		String topic = ChatConstants.REDIS_LISTEN_TOPIC;
		System.out.println("카프카로" + topic + "보냄 : " + clientMessage.toString());
		kafkaTemplate.send(topic, clientMessage);
	}
}
