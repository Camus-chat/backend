package com.camus.backend.model.service;

import java.util.List;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;

import com.camus.backend.chat.domain.document.CommonMessage;

public interface ModelService {
	public void token(List<CommonMessage> messages, FutureCallback<SimpleHttpResponse> futureCallback);
	public void predict(List<CommonMessage> messages, FutureCallback<SimpleHttpResponse> futureCallback);
	public void predict(CommonMessage message, FutureCallback<SimpleHttpResponse> futureCallback);
}
