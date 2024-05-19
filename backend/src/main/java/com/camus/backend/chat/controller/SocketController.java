package com.camus.backend.chat.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.camus.backend.chat.domain.message.ClientToStompMessage;
import com.camus.backend.chat.domain.message.ClientToStompSubRequest;
import com.camus.backend.chat.domain.message.StompToRedisMessage;
import com.camus.backend.chat.service.KafkaConsumer.KafkaStompConsumerService;
import com.camus.backend.chat.service.KafkaProducer.KafkaStompProducerService;
import com.camus.backend.global.jwt.util.JwtTokenProvider;
import com.camus.backend.member.domain.document.MemberCredential;
import com.camus.backend.member.domain.dto.CustomUserDetails;
import com.camus.backend.member.domain.repository.MemberCredentialRepository;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
public class SocketController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SocketController.class);

	private final KafkaStompProducerService kafkaStompProducerService;
	private final KafkaStompConsumerService kafkaStompConsumerService;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberCredentialRepository memberCredentialRepository;

	public SocketController(KafkaStompProducerService kafkaStompProducerService,
		KafkaStompConsumerService kafkaStompConsumerService, JwtTokenProvider jwtTokenProvider,
		MemberCredentialRepository memberCredentialRepository) {
		this.kafkaStompProducerService = kafkaStompProducerService;
		this.kafkaStompConsumerService = kafkaStompConsumerService;
		this.jwtTokenProvider = jwtTokenProvider;
		this.memberCredentialRepository = memberCredentialRepository;
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

		// 요청을 한 사용자의 uuid 구하기
		// Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		// UUID userUuid = userDetails.get_id();
		try {
			String username = jwtTokenProvider.getUsername(clientMessage.getUserToken());

			MemberCredential memberCredential = memberCredentialRepository.findByUsername(username);
			UUID userUuid = memberCredential.get_id();

			StompToRedisMessage stompToRedisMessage = StompToRedisMessage.builder()
				.roomId(clientMessage.getRoomId())
				.content(clientMessage.getContent())
				.userId(
					userUuid.toString()
				)
				.build();

			// kafka에 올리기
			kafkaStompProducerService.sendMessage(stompToRedisMessage);
		} catch (Exception e) {
			System.err.println("AccessToken이 없거나 유효하지 않습니다: " + e.getMessage());
		}

	}

	@MessageMapping("/message_received")
	public void subscribeToTopic(
		ClientToStompSubRequest clientToStompSubRequest
		// 여기도 사용자 인증 객체 등록
	) {

		// 요청을 한 사용자의 uuid 구하기
		// Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		// UUID userUuid = userDetails.get_id();
		try {
			String username = jwtTokenProvider.getUsername(clientToStompSubRequest.getUserToken());
			MemberCredential memberCredential = memberCredentialRepository.findByUsername(username);
			UUID userUuid = memberCredential.get_id();

			// KafkaConsumerService를 사용하여 특정 토픽 구독
			kafkaStompConsumerService.addListener(
				clientToStompSubRequest,
				userUuid
			);
		} catch (Exception e) {
			System.err.println("AccessToken이 없거나 유효하지 않습니다: " + e.getMessage());
		}
	}

}
