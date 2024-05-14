package com.camus.backend.filter.service;

import java.util.List;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;

import com.camus.backend.chat.domain.document.CommonMessage;

public interface FilterService {
	void token(List<CommonMessage> messages, FutureCallback<SimpleHttpResponse> futureCallback);
	void predict(List<CommonMessage> messages, FutureCallback<SimpleHttpResponse> futureCallback);
	void predict(CommonMessage message, FutureCallback<SimpleHttpResponse> futureCallback);
	boolean isBadWord(CommonMessage message);

}
