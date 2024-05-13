package com.camus.backend.stomp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final ConcurrentKafkaListenerContainerFactory<String, String> factory;

	public KafkaConsumerService(SimpMessagingTemplate simpMessagingTemplate,
		ConcurrentKafkaListenerContainerFactory<String, String> factory) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.factory = factory;
	}

	public void addListener(String topic, String groupId) {
		ConcurrentMessageListenerContainer<String, String> container = factory.createContainer(topic);
		container.getContainerProperties().setGroupId(groupId);
		container.setupMessageListener((MessageListener<String, String>) message -> {
			String jsonValue = message.value();
			System.out.println("Received dynamic message : " + message);
			simpMessagingTemplate.convertAndSend("/subscribe/message_receive/" + topic, jsonValue);
		});
		container.start();
	}
}
