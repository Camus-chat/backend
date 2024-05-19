package com.camus.backend.chat.util;


import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.global.jwt.util.JwtTokenProvider;
import com.camus.backend.member.domain.document.MemberCredential;
import com.camus.backend.member.domain.dto.CustomUserDetails;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

	// 토큰 검증용 인터셉터
	
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {

		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		// CONNECT 명령 시 JWT 토큰을 검증하고, 유효한 경우 SecurityContextHolder에 사용자 정보를 저장
		// if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
		//
		// 	// access token 꺼내오기
		// 	String accessToken = accessor.getFirstNativeHeader("access");
		// 	// 토큰이 없다면
		// 	if (accessToken == null) {
		// 		throw new CustomException(ErrorCode.NOTFOUND_TOKEN);
		// 	}
		//
		// 	// 토큰 만료 검사
		// 	try {
		// 		jwtTokenProvider.isExpired(accessToken);
		// 	} catch (ExpiredJwtException e) {
		// 		throw new CustomException(ErrorCode.FORBIDDEN_TOKEN_EXPIRED);
		// 	}
		//
		// 	// username, role 값을 획득
		// 	String username = jwtTokenProvider.getUsername(accessToken);
		// 	String role = jwtTokenProvider.getRole(accessToken);
		//
		// 	// memberCredential를 생성하여 값 set
		// 	// 세션 처리를 위한 임시객체
		// 	MemberCredential memberCredential = MemberCredential.builder()
		// 		.username(username)
		// 		.role(role)
		// 		.build();
		//
		// 	//UserDetails에 회원 정보 객체 담기
		// 	CustomUserDetails customUserDetails = new CustomUserDetails(memberCredential);
		//
		// 	//스프링 시큐리티 인증 토큰 생성
		// 	Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
		// 		customUserDetails.getAuthorities());
		// 	//세션에 사용자 등록
		// 	SecurityContextHolder.getContext().setAuthentication(authToken);
		// }else{
		// 	// CONNECT 명령이 아닌 경우, SecurityContextHolder를 통해 사용자가 인증되었는지 확인
		// 	checkUserIdAndRoleInSecurityContext();
		// }
		return message;
	}

	private void checkUserIdAndRoleInSecurityContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}
	}

}
