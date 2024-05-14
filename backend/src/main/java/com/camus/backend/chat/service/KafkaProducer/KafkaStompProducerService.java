package com.camus.backend.chat.service.KafkaProducer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaStompProducerService {
	private final KafkaTemplate<String, String> kafkaTemplate;

	public KafkaStompProducerService(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendMessage(String messageJson, String topicName) {
		System.out.println("카프카로" + topicName + "보냄 : " + messageJson);
		kafkaTemplate.send(topicName, messageJson);
	}
}
