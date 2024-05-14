package com.camus.backend.model.service;

import org.apache.hc.core5.http.nio.AsyncRequestProducer;

public interface HttpService {
	public void sendAsyncHttpRequest(AsyncRequestProducer producer);
}
