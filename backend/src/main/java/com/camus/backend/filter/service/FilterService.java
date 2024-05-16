package com.camus.backend.filter.service;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;

import com.camus.backend.filter.domain.Request.ContextFilteringRequest;
import com.camus.backend.filter.domain.Request.SingleFilteringRequest;

public interface FilterService {
	void token(ContextFilteringRequest request);
	void predict(ContextFilteringRequest request);
	void predict(SingleFilteringRequest request);

}
