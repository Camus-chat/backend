package com.camus.backend.filter.util.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class HttpSetting {
	@Value("${clova.host}")
	private String clovaHost;
	@Value("${clova.api-key}")
	private String clovaApiKey;
	@Value("${clova.api-key-primary}")
	private String clovaApiKeyPrimaryVal;
	@Value("${lambda.endpoint}")
	private String lambdaEndpoint;
}
