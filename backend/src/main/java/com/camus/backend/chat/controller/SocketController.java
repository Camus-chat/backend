package com.camus.backend.chat.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.camus.backend.chat.domain.message.ClientToStompMessage;
import com.camus.backend.chat.domain.message.ClientToStompSubRequest;
import com.camus.backend.chat.domain.message.StompToRedisMessage;
import com.camus.backend.chat.service.KafkaConsumer.KafkaStompConsumerService;
import com.camus.backend.chat.service.KafkaProducer.KafkaStompProducerService;
import com.camus.backend.manage.util.ManageConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
public class SocketController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SocketController.class);

	private final KafkaStompProducerService kafkaStompProducerService;
	private final KafkaStompConsumerService kafkaStompConsumerService;
	private final ObjectMapper objectMapper;

	public SocketController(KafkaStompProducerService kafkaStompProducerService,
		KafkaStompConsumerService kafkaStompConsumerService,
		ObjectMapper objectMapper) {
		this.kafkaStompProducerService = kafkaStompProducerService;
		this.kafkaStompConsumerService = kafkaStompConsumerService;
		this.objectMapper = objectMapper;
	}

	// 새로운 사용자가 웹 소켓을 연결할 때 실행됨
	// @EventListener은 한개의 매개변수만 가질 수 있다.
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectEvent event) {
		System.out.println("소켓 연결 시작");
		LOGGER.info("Received a new web socket connection");
	}

	// 사용자가 웹 소켓 연결을 끊으면 실행됨
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		System.out.println("소켓 연결 끊음");
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = headerAccessor.getSessionId();

		LOGGER.info("sessionId Disconnected : {}", sessionId);
	}

	// /pub/message로 메시지 발행
	@MessageMapping("/message_send")
	public void sendMessage(
		ClientToStompMessage clientMessage
		// 인증자 정보

	) {

		// 사용자 정보 담아서 Kafka에 올리기
		UUID tempUUID = ManageConstants.tempMemUuid;

		StompToRedisMessage stompToRedisMessage = StompToRedisMessage.builder()
			.roomId(clientMessage.getRoomId())
			.content(clientMessage.getContent())
			.userId(

				tempUUID.toString()

			)
			.build();

		// kafka에 올리기
		kafkaStompProducerService.sendMessage(stompToRedisMessage);
	}

	@MessageMapping("/message_received")
	public void subscribeToTopic(
		ClientToStompSubRequest clientToStompSubRequest
		// 여기도 사용자 인증 객체 등록
	) {
		UUID tempUUID = ManageConstants.tempMemUuid;

		// KafkaConsumerService를 사용하여 특정 토픽 구독
		kafkaStompConsumerService.addListener(
			clientToStompSubRequest,
			tempUUID
		);
	}

}
