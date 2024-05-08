package com.camus.backend.global.jwt.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	// Refresh Token 저장
	// key=username
	public void storeRefreshToken(String key, String token, long expireTime){
		redisTemplate.opsForValue().set(key, token);
		redisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);
	}

	// Refresh Token 조회
	public String getRefreshToken(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	// Refresh Token 삭제
	public void deleteRefreshToken(String key) {
		redisTemplate.delete(key);
	}

	// refresh 있는지 확인
	public boolean isRefreshExist(String key){
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

}
