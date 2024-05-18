package com.camus.backend.global.jwt.controller;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.global.jwt.service.ReissueService;
import com.camus.backend.global.jwt.util.JwtSettings;
import com.camus.backend.global.jwt.util.JwtTokenProvider;
import com.camus.backend.global.jwt.service.RedisService;
import com.camus.backend.member.domain.repository.MemberCredentialRepository;

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
	private final MemberCredentialRepository memberCredentialRepository;
	private final ReissueService reissueService;

	public ReissueController(JwtTokenProvider jwtTokenProvider, RedisService redisService, JwtSettings jwtSettings,
		MemberCredentialRepository memberCredentialRepository, ReissueService reissueService) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisService = redisService;
		this.jwtSettings = jwtSettings;
		this.memberCredentialRepository = memberCredentialRepository;
		this.reissueService = reissueService;
	}

	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

		return reissueService.reissueToken(request, response);
	}

}