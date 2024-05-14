package com.camus.backend.model.service;

import java.util.List;

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

	@Override
	public void predict(List<CommonMessage> messages) {
		try {
			httpService.sendAsyncHttpRequest(modelRequestBuilder.getMessagesTokenRequest(new ClovaCompletionRequest(messages)));
			httpService.sendAsyncHttpRequest(modelRequestBuilder.getMessagesPredictRequest(new ClovaCompletionRequest(messages)));
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void predict(CommonMessage message) {
		try {
			httpService.sendAsyncHttpRequest(modelRequestBuilder.getMessagePredictRequest(message));
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
