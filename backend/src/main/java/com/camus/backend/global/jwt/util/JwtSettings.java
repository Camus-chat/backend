package com.camus.backend.global.jwt.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class JwtSettings {
	@Value("${spring.jwt.expiration.accessExpire}")
	private long accessExpire;

	@Value("${spring.jwt.expiration.refreshExpire}")
	private long refreshExpire;

	@Value("${spring.jwt.expiration.guestExpire}")
	private long guestExpire;

}
