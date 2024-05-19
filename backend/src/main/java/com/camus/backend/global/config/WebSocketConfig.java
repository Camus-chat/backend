package com.camus.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.camus.backend.chat.util.StompHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	// private static final Logger LOGGER = LoggerFactory.getLogger( WebSocketConfig.class );
	private final StompHandler stompHandler;

	public WebSocketConfig(StompHandler stompHandler) {
		this.stompHandler = stompHandler;
	}

	// 클라이언트가 웹 소켓 서버에 연결하는데 사용할 웹 소켓 엔드포인트 등록
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		System.out.println("엔드포인트 설정");
		registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*")
			// client가 sockjs로 개발되어 있을 때만 필요, client가 java면 필요없음
			.withSockJS();
	}

	/*한 클라이언트에서 다른 클라이언트로 메시지를 라우팅하는데 사용될 메시지 브로커*/
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		System.out.println("pubsub 설정");
		// pub로 시작되는 메시지는 message-handling methods로 라우팅된다.
		registry.setApplicationDestinationPrefixes("/pub");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration){
		registration.interceptors(stompHandler);
	}

}