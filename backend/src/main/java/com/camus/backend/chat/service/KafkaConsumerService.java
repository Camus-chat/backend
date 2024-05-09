package com.camus.backend.chat.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
	private final SimpMessagingTemplate simpMessagingTemplate;

	public KafkaConsumerService(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	@KafkaListener(topics = "myTopic", groupId = "myGroup")
	public void listen(String message) {
		simpMessagingTemplate.convertAndSend("/subscribe/message", message);
		System.out.println("Received message : " + message);
	}
}
