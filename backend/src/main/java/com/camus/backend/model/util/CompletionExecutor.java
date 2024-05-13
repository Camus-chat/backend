package com.camus.backend.model.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CompletionExecutor {
	private String host;
	private String apiKey;
	private String apiKeyPrimaryVal;
	// private String requestId;
	private final String modelName = "HCX-003";
	ObjectMapper objectMapper = new ObjectMapper();
	public CompletionExecutor(String host, String apiKey, String apiKeyPrimaryVal) {
		this.host = host;
		this.apiKey = apiKey;
		this.apiKeyPrimaryVal = apiKeyPrimaryVal;
		// this.requestId = requestId;
	}

	public void token(CompletionRequest completionRequest){
		System.out.println(host);
		System.out.println(apiKey);
		System.out.println(apiKeyPrimaryVal);
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(host + "/testapp/v1/api-tools/chat-tokenize/"+modelName))
				.header("X-NCP-CLOVASTUDIO-API-KEY", apiKey)
				.header("X-NCP-APIGW-API-KEY", apiKeyPrimaryVal)
				// .header("X-NCP-CLOVASTUDIO-REQUEST-ID", "REQ" + System.currentTimeMillis())
				.header("Content-Type", "application/json; charset=utf-8")
				// .header("Accept", "text/event-stream")
				.POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(completionRequest)))
				.build();

			System.out.println(request.toString());
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println(response.body());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void execute(CompletionRequest completionRequest) {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(host + "/testapp/v1/tasks/a3r3v845/chat-completions"))
				.header("X-NCP-CLOVASTUDIO-API-KEY", apiKey)
				.header("X-NCP-APIGW-API-KEY", apiKeyPrimaryVal)
				// .header("X-NCP-CLOVASTUDIO-REQUEST-ID", "REQ" + System.currentTimeMillis())
				.header("Content-Type", "application/json; charset=utf-8")
				// .header("Accept", "text/event-stream")
				.POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(completionRequest)))
				.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println(response.body());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
