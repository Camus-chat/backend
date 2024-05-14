package com.camus.backend.model.service;

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

	public void sendAsyncHttpRequest(AsyncRequestProducer producer, FutureCallback<SimpleHttpResponse> futureCallback) {

		AsyncResponseConsumer<SimpleHttpResponse> consumer = SimpleResponseConsumer.create();
		httpClient.execute(producer, consumer, futureCallback);

	}
}
