package com.camus.backend.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	private final SecretKey secretKey;

	public JwtTokenProvider(@Value("${spring.jwt.secret}") String secret) {
		secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	// token에서 아이디 꺼내오기
	public String getUsername(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("username", String.class);
	}

	// token에서 역할 꺼내오기
	public String getRole(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("role", String.class);
	}

	// token 만료 확인
	public Boolean isExpired(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getExpiration()
			.before(new Date());
	}

	// 토큰 종류 가져오기
	public String getCategory(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("category", String.class);
	}

	// token 생성
	public String createToken(String category, String username, String role, Long expiredMs) {
		return Jwts.builder()
			.claim("category", category)
			.claim("username", username)
			.claim("role", role)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiredMs))
			.signWith(secretKey)
			.compact();
	}
}

// // accesstoken 생성
// public String createAccessToken(String username, String role) {
// 	return Jwts.builder()
// 		.claim("username", username)
// 		.claim("role", role)
// 		.issuedAt(new Date(System.currentTimeMillis()))
// 		.expiration(new Date(System.currentTimeMillis() + accessExpire))
// 		.signWith(secretKey)
// 		.compact();
// }
//
// // refreshtoken 생성
// public String createRefreshToken(String username) {
// 	return Jwts.builder()
// 		.claim("username", username)
// 		.issuedAt(new Date(System.currentTimeMillis()))
// 		.expiration(new Date(System.currentTimeMillis() + refreshExpire))
// 		.signWith(secretKey)
// 		.compact();
// }
