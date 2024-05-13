package com.camus.backend.global.jwt.service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.global.jwt.util.JwtSettings;
import com.camus.backend.global.jwt.util.JwtTokenProvider;
import com.camus.backend.global.util.SuccessCode;
import com.camus.backend.member.domain.repository.MemberCredentialRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class ReissueService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisService redisService;
	private final JwtSettings jwtSettings;
	private final MemberCredentialRepository memberCredentialRepository;

	public ReissueService(JwtTokenProvider jwtTokenProvider, RedisService redisService, JwtSettings jwtSettings,
		MemberCredentialRepository memberCredentialRepository) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisService = redisService;
		this.jwtSettings = jwtSettings;
		this.memberCredentialRepository = memberCredentialRepository;
	}

	public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response){

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
			//return new ResponseEntity<>("refresh token 없음", HttpStatus.BAD_REQUEST);
			throw new CustomException(ErrorCode.NOTFOUND_TOKEN);
		}

		String accessUsername = jwtTokenProvider.getUsername(refreshToken);

		//만료 확인
		try {
			jwtTokenProvider.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			// 만료된 refresh 삭제
			redisService.deleteRefreshToken(accessUsername);
			throw new CustomException(ErrorCode.FORBIDDEN_TOKEN_EXPIRED);
			//return new ResponseEntity<>("refresh token 만료됨", HttpStatus.BAD_REQUEST);
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtTokenProvider.getCategory(refreshToken);
		if (!category.equals("refresh")) {
			//return new ResponseEntity<>("유효하지 않은 refresh token", HttpStatus.BAD_REQUEST);
			throw new CustomException(ErrorCode.INVALID_PARAMETER_TOKEN);
		}

		// redis에 refresh가 저장되어 있는지 확인
		if (!redisService.doesRefreshTokenNotExist(accessUsername)) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_TOKEN);
			//return new ResponseEntity<>("유효하지 않은 refresh token", HttpStatus.BAD_REQUEST);
		}

		String username = jwtTokenProvider.getUsername(refreshToken);
		String role = jwtTokenProvider.getRole(refreshToken);

		// 게스트면 계정이 5일동안만 유지
		if(role.equals("guest")){
			LocalDateTime nowDate = LocalDateTime.now();
			LocalDateTime createDate = memberCredentialRepository.findByUsername(username).getLoginTime();

			// nowDate와 createDate의 차이 계산
			Duration duration = Duration.between(nowDate, createDate);
			long durationToSecond = duration.toSeconds();

			// 5일이 지났으면 갱신 안함
			if(durationToSecond>432000){
				//return new ResponseEntity<>("게스트 계정은 5일동안만 이용 가능합니다", HttpStatus.BAD_REQUEST);
				throw new CustomException(ErrorCode.FORBIDDEN_GUEST);
			}
		}

		//token 새로 생성
		String newAccess = jwtTokenProvider.createToken("access", username, role, jwtSettings.getAccessExpire());
		String newRefresh = jwtTokenProvider.createToken("refresh", username, role, jwtSettings.getRefreshExpire());

		// 기존 refresh 삭제 후 새 refresh를 redis에 넣기
		redisService.deleteRefreshToken(accessUsername);
		redisService.storeRefreshToken(accessUsername, newRefresh, jwtSettings.getRefreshExpire());

		//response에 넣기
		response.setHeader("access", newAccess);
		response.addCookie(createCookie("refresh", newRefresh, jwtSettings.getRefreshExpire()));

		// 어떤 유저인지 주기
		Map<String, String> tokenDetails = new HashMap<>();
		tokenDetails.put("role", role);
		response.setStatus(HttpStatus.OK.value());
		response.setContentType("application/json");
		try {
			new ObjectMapper().writeValue(response.getOutputStream(), tokenDetails);
		} catch (IOException e) {
			throw new CustomException(ErrorCode.NOTFOUND_USER);
		}

		return ResponseEntity.ok(tokenDetails);
	}

	private Cookie createCookie(String key, String value, long cookieRefreshTime) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge((int)cookieRefreshTime);
		cookie.setSecure(true); //https 통신할거면 넣기
		//cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
}
