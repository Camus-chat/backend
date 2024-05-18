package com.camus.backend.chat.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.camus.backend.chat.service.KafkaConsumer.KafkaStompConsumerService;
import com.camus.backend.chat.service.KafkaProducer.KafkaStompProducerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
	public void sendMessage(Map<String, Object> params) throws JsonProcessingException {
		System.out.println("카프카로 채팅 쳤다");
		// 구독중인 client에 메세지를 보낸다.
		String topic = params.get("topic").toString();
		// 카프카에게 발송해주는 로직
		String messageJson = objectMapper.writeValueAsString(params);
		kafkaStompProducerService.sendMessage(messageJson, topic);
	}

	@MessageMapping("/message_received")
	public void subscribeToTopic(@Payload String message) throws JsonProcessingException {
		// JSON 문자열을 파싱하기 위한 ObjectMapper 인스턴스 생성
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(message);

		// "topic" 필드 값을 추출
		String topic = jsonNode.get("topic").asText();
		String groupId = "0";

		System.out.println("토픽 : " + topic);

		// KafkaConsumerService를 사용하여 특정 토픽 구독
		kafkaStompConsumerService.addListener(topic, groupId);
	}

	@MessageMapping("/message_stop")
	public void stopMessage() {
		kafkaStompConsumerService.stopContainer();
	}
}
