package com.camus.backend.model.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class ClovaSetting {
	@Value("${clova.host}")
	private String host;
	@Value("${clova.api-key}")
	private String apiKey;
	@Value("${clova.api-key-primary}")
	private String apiKeyPrimaryVal;
}
