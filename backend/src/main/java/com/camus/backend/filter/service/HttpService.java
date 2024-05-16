package com.camus.backend.filter.service;

import java.util.concurrent.CompletableFuture;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;

public interface HttpService {

	CompletableFuture<SimpleHttpResponse> sendAsyncHttpRequest(AsyncRequestProducer producer);
}
