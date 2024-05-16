package com.camus.backend.filter.service;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.springframework.stereotype.Service;

import com.camus.backend.filter.domain.Request.ContextFilteringRequest;
import com.camus.backend.filter.domain.Request.SingleFilteringRequest;
import com.camus.backend.filter.domain.Response.SingleFilteringResponse;
import com.camus.backend.filter.util.AhoCorasick;
import com.camus.backend.filter.util.BadWords;
import com.camus.backend.filter.util.component.FilterModule;
import com.camus.backend.filter.util.component.FilterRequestBuilder;
import com.camus.backend.filter.util.ClovaCompletionRequest;


@Service
public class FilterServiceImpl implements FilterService {
	private final FilterRequestBuilder filterRequestBuilder;
	private final HttpService httpService;
	private final AhoCorasick ahoCorasick;
	private final FilterModule filterModule;

	public FilterServiceImpl(FilterRequestBuilder modelRequestBuilder,
		HttpService httpService,
		FilterModule filterModule){
		this.filterRequestBuilder = modelRequestBuilder;
		this.httpService = httpService;
		ahoCorasick = new AhoCorasick(BadWords.koreaBadWords);
		this.filterModule = filterModule;
	}
	public void token(ContextFilteringRequest request) {
		try {
			httpService.sendAsyncHttpRequest(filterRequestBuilder.
				getMessagesTokenRequest(new ClovaCompletionRequest(request)),
				filterModule.GetTokenPredictCallback(request));
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void predict(ContextFilteringRequest request) {
		try {
			httpService.sendAsyncHttpRequest(filterRequestBuilder
				.getMessagesPredictRequest(new ClovaCompletionRequest(request)),
				filterModule.getContextPredictCallback(request));
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void predict(SingleFilteringRequest request) {
		if (isBadWord(request)) return;
		try {
			httpService.sendAsyncHttpRequest(filterRequestBuilder
				.getMessagePredictRequest(request),
				filterModule.GetSinglePredictCallback(request));
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private boolean isBadWord(SingleFilteringRequest request) {
		boolean result = ahoCorasick.containsAny(request.getSimpleMessage().getContent());
		if (result){
			filterModule.sendSimpleFilteringResponse(request);
		}
		return result;
	}

}
