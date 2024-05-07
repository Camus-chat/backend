package com.camus.backend.stomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
	private final SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	public KafkaConsumerService(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	@KafkaListener(topics = "myTopic", groupId = "myGroup")
	public void listen(String message) {
		simpMessagingTemplate.convertAndSend("/subscribe/message", message);
		System.out.println("Received message : " + message);
	}
}
