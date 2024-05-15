package com.camus.backend.filter.util.component;

import java.util.Map;

import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.entity.StringAsyncEntityProducer;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;
import org.springframework.stereotype.Component;

import com.camus.backend.filter.domain.Request.SingleFilteringRequest;
import com.camus.backend.filter.util.ClovaCompletionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.core5.http.ContentType;

import lombok.Getter;

@Getter
@Component
public class FilterRequestBuilder {
	private final String clovaHost;
	private final String apiKey;
	private final String apiKeyPrimaryVal;
	// private String requestId;
	private final String lambdaEndpoint;

	private final String modelName = "HCX-003";
	private final String tokenString = "/testapp/v1/api-tools/chat-tokenize/HCX-003";
	private final String evalString = "/testapp/v1/tasks/a3r3v845/chat-completions";


	private final ObjectMapper objectMapper;
	// public ModelRequestBuilder(String host, String apiKey, String apiKeyPrimaryVal) {
	//
	// 	// this.requestId = requestId;
	// }
	public FilterRequestBuilder(HttpSetting httpSetting, ObjectMapper objectMapper){
		clovaHost = httpSetting.getClovaHost();
		apiKey = httpSetting.getClovaApiKey();
		apiKeyPrimaryVal = httpSetting.getClovaApiKeyPrimaryVal();
		lambdaEndpoint = httpSetting.getLambdaEndpoint();
		this.objectMapper = objectMapper;
	}

	public AsyncRequestProducer getMessagesTokenRequest(ClovaCompletionRequest clovaCompletionRequest) throws Exception {
		return AsyncRequestBuilder.post(clovaHost + tokenString)
			.setHeader("X-NCP-CLOVASTUDIO-API-KEY", apiKey)
			.setHeader("X-NCP-APIGW-API-KEY", apiKeyPrimaryVal)
			// .setHeader("X-NCP-CLOVASTUDIO-REQUEST-ID", "REQ" + System.currentTimeMillis())
			// .setHeader("Content-Type", "application/json; charset=utf-8")
			// .setHeader("Accept", "text/event-stream")
			.setEntity(new StringAsyncEntityProducer(objectMapper.writeValueAsString(clovaCompletionRequest), ContentType.APPLICATION_JSON))
			.build();
	}

	public AsyncRequestProducer getMessagesPredictRequest(ClovaCompletionRequest clovaCompletionRequest) throws  Exception {
		return AsyncRequestBuilder.post(clovaHost + evalString)
			.setHeader("X-NCP-CLOVASTUDIO-API-KEY", apiKey)
			.setHeader("X-NCP-APIGW-API-KEY", apiKeyPrimaryVal)
			// .setHeader("X-NCP-CLOVASTUDIO-REQUEST-ID", "REQ" + System.currentTimeMillis())
			// .setHeader("Content-Type", "application/json; charset=utf-8")
			// .setHeader("Accept", "text/event-stream")
			.setEntity(new StringAsyncEntityProducer(objectMapper.writeValueAsString(clovaCompletionRequest), ContentType.APPLICATION_JSON))
			.build();
	}

	public AsyncRequestProducer getMessagePredictRequest(SingleFilteringRequest request) throws  Exception {
		return AsyncRequestBuilder.post(lambdaEndpoint)
			.setEntity(new StringAsyncEntityProducer(objectMapper.writeValueAsString(Map.of("text",request.getSimpleMessage().getContent())), ContentType.APPLICATION_JSON))
			.build();
	}
}
