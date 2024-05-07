package com.camus.backend.stomp;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
public class SocketController {

	private static final Logger LOGGER = LoggerFactory.getLogger( SocketController.class );

	// private final SimpMessageSendingOperations simpleMessageSendingOperations;
	private final SimpMessagingTemplate messagingTemplate;
	private final KafkaProducerService kafkaProducerService;
	private final ObjectMapper objectMapper;

    public SocketController(SimpMessagingTemplate simpMessagingTemplate, KafkaProducerService kafkaProducerService,
		ObjectMapper objectMapper) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.kafkaProducerService = kafkaProducerService;
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
	@MessageMapping("/message")
	public void sendMessage(Map<String, Object> params) throws JsonProcessingException {
		System.out.println("카프카로 채팅 쳤다");
		// 구독중인 client에 메세지를 보낸다.

		// 카프카에게 발송해주는 로직
		String messageJson = objectMapper.writeValueAsString(params);
		kafkaProducerService.sendMessage(messageJson, "myTopic");

		// 클라이언트에게 직접 메시지 발송해주는 로직
		String channelId = params.get("channelId").toString();
		messagingTemplate.convertAndSend("/subscribe/message/" + channelId, params);
	}

	/*
	// /pub/cache 로 메시지를 발행한다.
	@MessageMapping("/message")
	// simple broker용
	// @SendTo("/sub/cache")
	public void sendMessage(Map<String, Object> message) {
		System.out.println("채팅 쳤다");
		// 구독중인 client에 메세지를 보낸다.
		// simpleMessageSendingOperations.convertAndSend("/subscribe/message/" + message.get("channelId"), message);
		// System.out.println(message);
		kafkaProducerService.sendMessage(message.toString());
	}
	*/
}
