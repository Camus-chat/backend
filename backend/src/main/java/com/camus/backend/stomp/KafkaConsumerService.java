package com.camus.backend.stomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
	private final SimpMessagingTemplate template;

	@Autowired
	public KafkaConsumerService(SimpMessagingTemplate template) {
		this.template = template;
	}

	@KafkaListener(topics = "myTopic", groupId = "myGroup")
	public void listen(String message) {
		template.convertAndSend("/subscribe/message", message);
		System.out.println("Received message : " + message);
	}
}
