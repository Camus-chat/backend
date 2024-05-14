package com.camus.backend.filter.service;

import java.util.List;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.filter.util.AhoCorasick;
import com.camus.backend.filter.util.BadWords;
import com.camus.backend.filter.util.component.FilterRequestBuilder;
import com.camus.backend.filter.util.ClovaCompletionRequest;

@Service
public class FilterServiceImpl implements FilterService {
	private final FilterRequestBuilder filterRequestBuilder;
	private final HttpService httpService;
	private final AhoCorasick ahoCorasick;

	public FilterServiceImpl(FilterRequestBuilder modelRequestBuilder, HttpService httpService){
		this.filterRequestBuilder = modelRequestBuilder;
		this.httpService = httpService;
		ahoCorasick = new AhoCorasick(BadWords.koreaBadWords);
	}
	public void token(List<CommonMessage> messages, FutureCallback<SimpleHttpResponse> futureCallback) {
		try {
			httpService.sendAsyncHttpRequest(filterRequestBuilder.
					getMessagesTokenRequest(new ClovaCompletionRequest(messages))
				, futureCallback);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void predict(List<CommonMessage> messages, FutureCallback<SimpleHttpResponse> futureCallback) {
		try {
			httpService.sendAsyncHttpRequest(filterRequestBuilder.getMessagesPredictRequest(new ClovaCompletionRequest(messages))
			, futureCallback);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void predict(CommonMessage message, FutureCallback<SimpleHttpResponse> futureCallback) {
		try {
			httpService.sendAsyncHttpRequest(filterRequestBuilder.getMessagePredictRequest(message), futureCallback);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public boolean isBadWord(CommonMessage message) {
		return ahoCorasick.containsAny(message.getContent());
	}

}
