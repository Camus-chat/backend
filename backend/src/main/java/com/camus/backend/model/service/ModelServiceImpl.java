package com.camus.backend.model.service;

import java.util.List;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.model.util.ModelRequestBuilder;
import com.camus.backend.model.util.ClovaCompletionRequest;

@Service
public class ModelServiceImpl implements ModelService {
	private final ModelRequestBuilder modelRequestBuilder;
	private final HttpService httpService;
	public ModelServiceImpl(ModelRequestBuilder modelRequestBuilder, HttpService httpService){
		this.modelRequestBuilder = modelRequestBuilder;
		this.httpService = httpService;
	}
	public void token(List<CommonMessage> messages, FutureCallback<SimpleHttpResponse> futureCallback) {
		try {
			httpService.sendAsyncHttpRequest(modelRequestBuilder.
					getMessagesTokenRequest(new ClovaCompletionRequest(messages))
				, futureCallback);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void predict(List<CommonMessage> messages, FutureCallback<SimpleHttpResponse> futureCallback) {
		try {
			httpService.sendAsyncHttpRequest(modelRequestBuilder.getMessagesPredictRequest(new ClovaCompletionRequest(messages))
			, futureCallback);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void predict(CommonMessage message, FutureCallback<SimpleHttpResponse> futureCallback) {
		try {
			httpService.sendAsyncHttpRequest(modelRequestBuilder.getMessagePredictRequest(message), futureCallback);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
