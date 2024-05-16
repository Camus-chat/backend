package com.camus.backend.filter.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.camus.backend.filter.domain.Request.ContextFilteringRequest;
import com.camus.backend.filter.domain.Request.SingleFilteringRequest;
import com.camus.backend.filter.domain.Response.ContextFilteringResponse;
import com.camus.backend.filter.domain.Response.SingleFilteringResponse;
import com.camus.backend.filter.service.kafka.KafkaFilterProducer;
import com.camus.backend.filter.util.AhoCorasick;
import com.camus.backend.filter.util.BadWords;
import com.camus.backend.filter.util.component.FilterRequestBuilder;
import com.camus.backend.filter.util.ClovaCompletionRequest;
import com.camus.backend.filter.util.component.StatisticConstants;
import com.camus.backend.filter.util.type.ContextFilteringType;
import com.camus.backend.filter.util.type.FilteredType;
import com.camus.backend.filter.util.type.SingleFilteringType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FilterServiceImpl implements FilterService {
	private final FilterRequestBuilder filterRequestBuilder;
	private final HttpService httpService;
	private final AhoCorasick ahoCorasick;
	private final ObjectMapper objectMapper;
	private final KafkaFilterProducer kafkaFilterProducer;
	public final StatisticConstants statisticConstants;
	public final RedisTemplate<String, Long> redisTemplate;

	public FilterServiceImpl(
		FilterRequestBuilder modelRequestBuilder,
		HttpService httpService,
		ObjectMapper objectMapper,
		KafkaFilterProducer kafkaFilterProducer,
		StatisticConstants statisticConstants,
		RedisTemplate<String, Long> redisTemplate
	){
		this.filterRequestBuilder = modelRequestBuilder;
		this.httpService = httpService;
		ahoCorasick = new AhoCorasick(BadWords.koreaBadWords);
		this.objectMapper = objectMapper;
		this.kafkaFilterProducer = kafkaFilterProducer;
		this.statisticConstants = statisticConstants;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void predict(ContextFilteringRequest request) throws Exception {
		ClovaCompletionRequest clovaRequest = new ClovaCompletionRequest(request);
		httpService.sendAsyncHttpRequest(filterRequestBuilder
			.getMessagesTokenRequest(clovaRequest))
			.thenAccept((simpleHttpResponse)-> {
				try {
					if (simpleHttpResponse.getCode() == 200) {
						precessTokenResponse(request, clovaRequest, simpleHttpResponse);
					}else throw new RuntimeException("clova token api error");
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
	}

	@Override
	public void predict(SingleFilteringRequest request) throws Exception {
		if (isBadWord(request)) return;
		httpService.sendAsyncHttpRequest(filterRequestBuilder
			.getMessagePredictRequest(request))
			.thenAccept((simpleHttpResponse)-> {
				try {
					if (simpleHttpResponse.getCode() == 200) {

						Map<?, ?> map = objectMapper.readValue(simpleHttpResponse.getBody()
							.getBodyText(), Map.class);
						SingleFilteringResponse response;
						switch (SingleFilteringType.fromValue((int)map.get("prediction"))) {
							case MALICIOUS -> response = new SingleFilteringResponse(request,
								FilteredType.MALICIOUS_LAMBDA);
							case HATE -> response = new SingleFilteringResponse(request,
								FilteredType.HATE_LAMBDA);
							case NOT_FILTERED -> response = new SingleFilteringResponse(request,
								FilteredType.NOT_FILTERED);
							default -> throw new RuntimeException("Unexpected filtering type");
						}
						kafkaFilterProducer.sendResponse(response);
					}else throw new RuntimeException("lambda predict api error");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			});
	}

	private void precessTokenResponse(
		ContextFilteringRequest request,
		ClovaCompletionRequest clovaRequest,
		SimpleHttpResponse simpleHttpResponse
	) throws Exception {
		Map<?, ?> map = objectMapper.readValue(simpleHttpResponse.getBody().getBodyText(), Map.class);
		if (((Map<?, ?>)map.get("status")).get("code").equals("20000")) {
			List<?> messages = (List<?>)((Map<?, ?>)map.get("result")).get("messages");

			int count = (int)((Map<?, ?>)messages.get(0)).get("count");
			reTriggerPrediction(request, clovaRequest, count);
		} else throw new RuntimeException();
	}
	private void reTriggerPrediction(
		ContextFilteringRequest request,
		ClovaCompletionRequest clovaRequest,
		int count
	) throws Exception {
		httpService.sendAsyncHttpRequest(filterRequestBuilder
				.getMessagesPredictRequest(clovaRequest))
			.thenAccept( (response)-> {
				try {
					if (response.getCode() == 200) {
						processClovaPredictResponse(request, response, count);
					} else throw new RuntimeException("clova predict api error");
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
	}
	private void processClovaPredictResponse(
		ContextFilteringRequest request,
		SimpleHttpResponse simpleHttpResponse,
		int count
	) throws Exception{
		Map<?, ?> map = objectMapper.readValue(simpleHttpResponse.getBody().getBodyText(), Map.class);
		if (((Map<?, ?>)map.get("status")).get("code").equals("20000")){
			Map<?,?> message = (Map<?,?>)((Map<?, ?>)map.get("result")).get("message");
			String[] resultArr = ((String)message.get("content")).split(",");
			if (resultArr.length != request.getUserMessages().size()) throw new RuntimeException("clova predict failed");
			FilteredType[] resultTypeArr = new FilteredType[resultArr.length];
			for (int i=0; i<resultArr.length; i++){
				switch (ContextFilteringType.fromString(resultArr[i])){
					case MALICIOUS -> resultTypeArr[i] = FilteredType.MALICIOUS_CLOVA;
					case HATE -> resultTypeArr[i] = FilteredType.HATE_CLOVA;
					case SPAM -> resultTypeArr[i] = FilteredType.SPAM;
					case NOT_FILTERED -> resultTypeArr[i] = FilteredType.NOT_FILTERED;
				}
			}
			kafkaFilterProducer.sendResponse(new ContextFilteringResponse(request, resultTypeArr));
			// 여기서 redis 처리
		}
	}

	private boolean isBadWord(SingleFilteringRequest request) {
		boolean result = ahoCorasick.containsAny(request.getSimpleMessage().getContent());
		if (result){
			kafkaFilterProducer.sendResponse(
				new SingleFilteringResponse(request, FilteredType.MALICIOUS_SIMPLE));
		}
		return result;
	}
}
