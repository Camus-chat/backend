package com.camus.backend.chat.service.KafkaConsumer;

import java.util.UUID;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.message.ClientToStompSubRequest;
import com.camus.backend.chat.util.ChatModules;

@Service
public class KafkaStompConsumerService {
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final ConcurrentKafkaListenerContainerFactory<String, String> factory;

	private final ChatModules chatModules;

	public KafkaStompConsumerService(SimpMessagingTemplate simpMessagingTemplate,
		ConcurrentKafkaListenerContainerFactory<String, String> factory,
		ChatModules chatModules) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.factory = factory;
		this.chatModules = chatModules;
	}

	public void addListener(
		ClientToStompSubRequest clientToStompSubRequest,
		UUID userId
	) {
		// FIXME : 중복요청이 들어왔을때 처리

		// container 생성
		ConcurrentMessageListenerContainer<String, String> container
			= factory.createContainer(
			chatModules.getRedisToClientRoomTopic(
				clientToStompSubRequest.getRoomId()
			)
		);

		// container 설정
		container.getContainerProperties().setGroupId(
			chatModules.getClientGroupIdByRoomId(
				clientToStompSubRequest.getRoomId()
			)
		);

		UUID roomIdToUUID = clientToStompSubRequest.getRoomId();
		String roomIdTopic = chatModules.getRedisToClientRoomTopic(roomIdToUUID);
		String subTopic = "/subscribe/message_receive/" + clientToStompSubRequest.getRoomId();

		container.setupMessageListener((MessageListener<String, String>)message -> {
			System.out.println("client가 메시지 받음 : " + message.value());
			simpMessagingTemplate.convertAndSend(subTopic, message.value());
		});
		container.start();

	}

}
