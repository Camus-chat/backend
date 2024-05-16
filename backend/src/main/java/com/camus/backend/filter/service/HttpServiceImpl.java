package com.camus.backend.filter.service;

import java.util.concurrent.CompletableFuture;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.springframework.stereotype.Service;

@Service
public class HttpServiceImpl implements HttpService {
	private final CloseableHttpAsyncClient httpClient;

	public HttpServiceImpl(CloseableHttpAsyncClient httpClient) {
		this.httpClient = httpClient;
	}

	public CompletableFuture<SimpleHttpResponse> sendAsyncHttpRequest(AsyncRequestProducer producer) {
		CompletableFuture<SimpleHttpResponse> futureResponse = new CompletableFuture<>();
		AsyncResponseConsumer<SimpleHttpResponse> consumer = SimpleResponseConsumer.create();
		httpClient.execute(producer, consumer, new FutureCallback<>() {
			@Override
			public void completed(SimpleHttpResponse result) {
				futureResponse.complete(result);
			}

			@Override
			public void failed(Exception ex) {
				futureResponse.completeExceptionally(ex);
			}

			@Override
			public void cancelled() {
				futureResponse.cancel(true);
			}
		});

		return futureResponse;

	}
}
