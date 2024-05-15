package com.camus.backend.filter.util.component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.springframework.stereotype.Component;

import com.camus.backend.filter.domain.Request.ContextFilteringRequest;
import com.camus.backend.filter.domain.Request.SingleFilteringRequest;
import com.camus.backend.filter.domain.Response.ContextFilteringResponse;
import com.camus.backend.filter.domain.Response.FilteredMessage;
import com.camus.backend.filter.domain.Response.SingleFilteringResponse;
import com.camus.backend.filter.util.type.ContextFilteringType;
import com.camus.backend.filter.util.type.FilteredType;
import com.camus.backend.filter.util.type.FilteringLevel;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class FilterModule {
	private final ObjectMapper objectMapper;

	public FilterModule(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public FutureCallback<SimpleHttpResponse> GetSinglePredictCallback(SingleFilteringRequest request) {
		return new FutureCallback<>() {
			@Override
			public void completed(SimpleHttpResponse simpleHttpResponse) {
				if (simpleHttpResponse.getCode() == 200) {
					try {
						Map<?, ?> map = objectMapper.readValue(simpleHttpResponse.getBody().getBodyText(), Map.class);
						int value = (int)map.get("prediction");
						SingleFilteringResponse response;
						if (value == 0) {
							response = new SingleFilteringResponse(request,
								FilteredType.MALICIOUS_LAMBDA);
						} else if (value == 1 && request.getFilteringLevel() == FilteringLevel.HIGH) {
							response = new SingleFilteringResponse(request,
								FilteredType.HATE_LAMBDA);
							System.out.println(response.getFilteredMessage());
						} else if (value == 2) {
							response = new SingleFilteringResponse(request,
								FilteredType.NOT_FILTERED);
						} else {
							throw new RuntimeException("lambda predict api error");
						}
						// 이부분에서 결과 확인
						System.out.println("filterModule:" + response.getFilteredMessage());
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
		ContextFilteringRequest contextFilteringRequest) {
		return new FutureCallback<>() {
			@Override
			public void completed(SimpleHttpResponse simpleHttpResponse) {
				if (simpleHttpResponse.getCode() == 200) {
					try {
						Map<?, ?> map = objectMapper.readValue(simpleHttpResponse.getBody().getBodyText(), Map.class);
						// System.out.println(simpleHttpResponse.getBody().getBodyText());
						// System.out.println(map);
						if (((Map<?, ?>)map.get("status")).get("code").equals("20000")) {
							List<?> messages = (List<?>)((Map<?, ?>)map.get("result")).get("messages");
							int count = (int)((Map<?, ?>)messages.get(0)).get("count");
							System.out.println("count: " + count);
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}

				} else
					throw new RuntimeException("clova token api error");
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
						System.out.println(simpleHttpResponse.getBody().getBodyText());
						System.out.println(map);
						if (((Map<?, ?>)map.get("status")).get("code").equals("20000")){
							Map<?,?> message = (Map<?,?>)((Map<?, ?>)map.get("result")).get("message");
							String[] resultArr = ((String)message.get("content")).split(",");
							if (resultArr.length != request.getUserMessages().size()) throw new RuntimeException("clova predict failed");
							FilteredType[] resultTypeArr = new FilteredType[resultArr.length];
							for (int i=0; i<resultArr.length; i++){
								switch (ContextFilteringType.fromString(resultArr[i])){
									case MALICIOUS -> resultTypeArr[i] = FilteredType.MALICIOUS_CLOVA;
									case HATE -> {
										if (request.getFilteringLevel()==FilteringLevel.HIGH) {
											resultTypeArr[i] = FilteredType.HATE_CLOVA;
										} else resultTypeArr[i] = FilteredType.NOT_FILTERED;
									}
									case SPAM -> resultTypeArr[i] = FilteredType.SPAM;
									case NOT_FILTERED -> resultTypeArr[i] = FilteredType.NOT_FILTERED;
								}
							}
							ContextFilteringResponse contextFilteringResponse = new ContextFilteringResponse(request, resultTypeArr);
							System.out.println(Arrays.toString(resultTypeArr));
							System.out.println(contextFilteringResponse.getFilteredMessages());
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
