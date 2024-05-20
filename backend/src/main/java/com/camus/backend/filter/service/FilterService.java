package com.camus.backend.filter.service;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;

import com.camus.backend.filter.domain.Request.ContextFilteringRequest;
import com.camus.backend.filter.domain.Request.SingleFilteringRequest;

public interface FilterService {
	void predict(ContextFilteringRequest request) throws Exception;
	void predict(SingleFilteringRequest request) throws Exception;

}
