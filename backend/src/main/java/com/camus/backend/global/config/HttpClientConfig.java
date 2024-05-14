package com.camus.backend.global.config;

import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;

@Configuration
public class HttpClientConfig {

	private CloseableHttpAsyncClient client;

	@Bean
	public CloseableHttpAsyncClient httpAsyncClient() {
		CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
		client.start(); // 클라이언트를 시작합니다.
		return client;
	}

	@PreDestroy
	public void closeHttpClient() {
		try {
			if (client != null) {
				client.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}