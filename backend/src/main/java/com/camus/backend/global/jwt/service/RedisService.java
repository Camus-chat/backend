package com.camus.backend.global.jwt.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
	
	// 해시를 사용해 refresh token 저장함
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	// 해시 키
	private static final String REFRESH_TOKEN_KEY = "refreshToken:";
	
	// Refresh Token 저장
	// key=username
	public void storeRefreshToken(String key, String token, long expireTime){
		// redisTemplate.opsForValue().set(key, token);
		// redisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);
		String newKey = REFRESH_TOKEN_KEY + key;
		redisTemplate.opsForValue().set(newKey, token, expireTime, TimeUnit.MILLISECONDS);
	}

	// Refresh Token 조회
	public String getRefreshToken(String key) {
		String newKey = REFRESH_TOKEN_KEY + key;
		return redisTemplate.opsForValue().get(newKey);
	}

	// Refresh Token 삭제
	public void deleteRefreshToken(String key) {
		String newKey = REFRESH_TOKEN_KEY + key;
		redisTemplate.delete(newKey);
	}

	// refresh 있는지 확인
	public boolean doesRefreshTokenNotExist(String key){
		String newKey = REFRESH_TOKEN_KEY + key;
		return Boolean.TRUE.equals(redisTemplate.hasKey(newKey));
	}


	// // Refresh Token 저장
	// // key=username
	// public void storeRefreshToken(String key, String token, long expireTime){
	// 	redisTemplate.opsForValue().set(key, token);
	// 	redisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);
	// }
	//
	// // Refresh Token 조회
	// public String getRefreshToken(String key) {
	// 	return redisTemplate.opsForValue().get(key);
	// }
	//
	// // Refresh Token 삭제
	// public void deleteRefreshToken(String key) {
	// 	redisTemplate.delete(key);
	// }
	//
	// // refresh 있는지 확인
	// public boolean isRefreshExist(String key){
	// 	return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	// }

}
