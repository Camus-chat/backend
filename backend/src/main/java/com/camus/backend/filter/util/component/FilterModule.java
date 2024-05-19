package com.camus.backend.filter.util.component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.springframework.stereotype.Component;

import com.camus.backend.filter.domain.Request.ContextFilteringRequest;
import com.camus.backend.filter.domain.Request.SingleFilteringRequest;
import com.camus.backend.filter.domain.Response.ContextCountingResponse;
import com.camus.backend.filter.domain.Response.ContextFilteringResponse;
import com.camus.backend.filter.domain.Response.FilteredMessage;
import com.camus.backend.filter.domain.Response.SingleFilteringResponse;
import com.camus.backend.filter.service.kafka.KafkaFilterProducer;
import com.camus.backend.filter.util.type.ContextFilteringType;
import com.camus.backend.filter.util.type.FilteredType;
import com.camus.backend.filter.util.type.SingleFilteringType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class FilterModule {
	private final ObjectMapper objectMapper;
	private final KafkaFilterProducer kafkaFilterProducer;
	public FilterModule(ObjectMapper objectMapper,
		KafkaFilterProducer kafkaFilterProducer) {
		this.objectMapper = objectMapper;
		this.kafkaFilterProducer = kafkaFilterProducer;
	}

	public void sendSimpleFilteringResponse(SingleFilteringRequest request){
		kafkaFilterProducer.sendResponse(new SingleFilteringResponse(request, FilteredType.MALICIOUS_SIMPLE));
	}

	public FutureCallback<SimpleHttpResponse> GetSinglePredictCallback(SingleFilteringRequest request) {
		return new FutureCallback<>() {
			@Override
			public void completed(SimpleHttpResponse simpleHttpResponse) {
				if (simpleHttpResponse.getCode() == 200) {
					try {
						Map<?, ?> map = objectMapper.readValue(simpleHttpResponse.getBody().getBodyText(), Map.class);
						SingleFilteringResponse response;
						switch (SingleFilteringType.fromValue((int)map.get("prediction"))){
							case MALICIOUS -> response = new SingleFilteringResponse(request,
								FilteredType.MALICIOUS_LAMBDA);
							case HATE -> response = new SingleFilteringResponse(request,
								FilteredType.HATE_LAMBDA);
							case NOT_FILTERED -> response = new SingleFilteringResponse(request,
								FilteredType.NOT_FILTERED);
							default -> throw new RuntimeException("Unexpected filtering type");
						}
						kafkaFilterProducer.sendResponse(response);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

				} else
					throw new RuntimeException("lambda predict api error");
			}

			@Override
			public void failed(Exception e) {
				System.out.println(e);
			}

			@Override
			public void cancelled() {
				System.out.println("clova predict cancelled");
			}
		};
	}

	public FutureCallback<SimpleHttpResponse> GetTokenPredictCallback(
		ContextFilteringRequest request) {
		return new FutureCallback<>() {
			@Override
			public void completed(SimpleHttpResponse simpleHttpResponse) {
				if (simpleHttpResponse.getCode() == 200) {
					try {
						Map<?, ?> map = objectMapper.readValue(simpleHttpResponse.getBody().getBodyText(), Map.class);
						if (((Map<?, ?>)map.get("status")).get("code").equals("20000")) {
							List<?> messages = (List<?>)((Map<?, ?>)map.get("result")).get("messages");
							int count = (int)((Map<?, ?>)messages.get(0)).get("count");
							System.out.println("count: " + count);
							kafkaFilterProducer.sendResponse(new ContextCountingResponse(request, count));
						} else throw new RuntimeException();
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				} else throw new RuntimeException("clova token api error");
			}

			@Override
			public void failed(Exception e) {
				System.out.println(e);
			}

			@Override
			public void cancelled() {
				System.out.println("clova token cancelled");
			}
		};
	}
	public FutureCallback<SimpleHttpResponse> getContextPredictCallback(
		ContextFilteringRequest request
	){
		return new FutureCallback<>() {
			@Override
			public void completed(SimpleHttpResponse simpleHttpResponse) {
				if (simpleHttpResponse.getCode()==200){
					try {
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
									case NOT_FILTERED -> resultTypeArr[i] = FilteredType.NOT_FILTERED;
								}
							}
							kafkaFilterProducer.sendResponse(new ContextFilteringResponse(request, resultTypeArr));
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}

				} else throw new RuntimeException("clova predict api error");
			}
			@Override
			public void failed(Exception e) {
				System.out.println(e);
			}
			@Override
			public void cancelled() {
				System.out.println("clova predict cancelled");
			}
		};
	}
}
