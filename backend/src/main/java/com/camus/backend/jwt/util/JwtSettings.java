package com.camus.backend.jwt.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtSettings {
	@Value("${spring.jwt.expiration.accessExpire}")
	private long accessExpire;

	@Value("${spring.jwt.expiration.refreshExpire}")
	private long refreshExpire;

	@Value("${spring.jwt.expiration.guestExpire}")
	private long guestExpire;

	public long getAccessExpire() {
		return accessExpire;
	}

	public long getRefreshExpire() {
		return refreshExpire;
	}

	public long getGuestExpire() {
		return guestExpire;
	}
}
