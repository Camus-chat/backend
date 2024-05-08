package com.camus.backend.global.jwt.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.global.jwt.util.JwtSettings;
import com.camus.backend.global.jwt.util.JwtTokenProvider;
import com.camus.backend.global.jwt.service.RedisService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ReissueController {

	// 토큰 재발급
	
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisService redisService;

	private final JwtSettings jwtSettings;

	public ReissueController(JwtTokenProvider jwtTokenProvider, RedisService redisService, JwtSettings jwtSettings) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisService = redisService;
		this.jwtSettings = jwtSettings;
	}

	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

		// String accessToken = request.getHeader("access");
		// String accessUsername = jwtTokenProvider.getUsername(accessToken);
		// String refresh = redisService.getRefreshToken(accessUsername);

		//refresh token 가져오기
		String refreshToken=null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("refresh")) {
				refreshToken = cookie.getValue();
			}
		}

		//토큰 없음
		if (refreshToken == null) {
			return new ResponseEntity<>("refresh token 없음", HttpStatus.BAD_REQUEST);
		}

		String accessUsername = jwtTokenProvider.getUsername(refreshToken);

		//만료 확인
		try {
			jwtTokenProvider.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			// 만료된 refresh 삭제
			redisService.deleteRefreshToken(accessUsername);
			return new ResponseEntity<>("refresh token 만료됨", HttpStatus.BAD_REQUEST);
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtTokenProvider.getCategory(refreshToken);
		if (!category.equals("refresh")) {
			return new ResponseEntity<>("유효하지 않은 refresh token", HttpStatus.BAD_REQUEST);
		}

		// redis에 refresh가 저장되어 있는지 확인
		if (!redisService.isRefreshExist(accessUsername)) {
			//response body
			return new ResponseEntity<>("유효하지 않은 refresh token", HttpStatus.BAD_REQUEST);
		}

		String username = jwtTokenProvider.getUsername(refreshToken);
		String role = jwtTokenProvider.getRole(refreshToken);

		//token 새로 생성
		String newAccess = jwtTokenProvider.createToken("access", username, role, jwtSettings.getAccessExpire());
		String newRefresh = jwtTokenProvider.createToken("refresh", username, role, jwtSettings.getRefreshExpire());

		// 기존 refresh 삭제 후 새 refresh를 redis에 넣기
		redisService.deleteRefreshToken(accessUsername);
		redisService.storeRefreshToken(accessUsername, newRefresh, jwtSettings.getRefreshExpire());

		//response에 넣기
		response.setHeader("access", newAccess);
		response.addCookie(createCookie("refresh", newRefresh, jwtSettings.getRefreshExpire()));

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private Cookie createCookie(String key, String value, long cookieRefreshTime) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge((int)cookieRefreshTime);
		//cookie.setSecure(true); https 통신할거면 넣기
		//cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}

}