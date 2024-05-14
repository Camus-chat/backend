package com.camus.backend.filter.service;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;

public interface HttpService {

	public void sendAsyncHttpRequest(AsyncRequestProducer producer, FutureCallback<SimpleHttpResponse> futureCallback);
}
