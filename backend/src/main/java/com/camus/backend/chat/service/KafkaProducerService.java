package com.camus.backend.chat.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

	private final KafkaTemplate<String, String> kafkaTemplate;

	public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendMessage(String messageJson, String topicName) {
		System.out.println("카프카로" + topicName + "보냄 : " + messageJson);
		kafkaTemplate.send(topicName, messageJson);
	}

	/*
	public void sendMessage(String message) {
		System.out.println("카프카로보냄 : " + message);
		kafkaTemplate.send("myTopic", message);
	}
	 */
}
