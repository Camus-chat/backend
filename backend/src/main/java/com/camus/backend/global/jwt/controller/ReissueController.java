package com.camus.backend.global.jwt.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.global.jwt.util.JwtTokenProvider;
import com.camus.backend.global.jwt.service.RedisService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ReissueController {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisService redisService;

	@Value("${spring.jwt.expiration.accessExpire}")
	private long accessExpire;

	public ReissueController(JwtTokenProvider jwtTokenProvider, RedisService redisService) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisService = redisService;
	}

	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

		//refresh token 가져오기
		String accessToken = request.getHeader("access");
		String accessUsername = jwtTokenProvider.getUsername(accessToken);
		String refresh = redisService.getRefreshToken(accessUsername);

		//토큰 없음
		if (refresh == null) {
			return new ResponseEntity<>("refresh token 없음", HttpStatus.BAD_REQUEST);
		}

		//만료 확인
		try {
			jwtTokenProvider.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			// 만료된 refresh 삭제
			redisService.deleteRefreshToken(accessUsername);
			return new ResponseEntity<>("refresh token 만료됨", HttpStatus.BAD_REQUEST);
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtTokenProvider.getCategory(refresh);
		if (!category.equals("refresh")) {
			return new ResponseEntity<>("유효하지 않은 refresh token", HttpStatus.BAD_REQUEST);
		}

		String username = jwtTokenProvider.getUsername(refresh);
		String role = jwtTokenProvider.getRole(refresh);

		//token 새로 생성
		String newAccess = jwtTokenProvider.createToken("access", username, role, accessExpire);

		//response에 넣기
		response.setHeader("access", newAccess);

		return new ResponseEntity<>(HttpStatus.OK);
	}
}