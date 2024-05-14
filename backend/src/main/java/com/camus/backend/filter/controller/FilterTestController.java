package com.camus.backend.filter.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.filter.service.FilterService;
import com.camus.backend.filter.util.type.FilteredType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("model")
public class FilterTestController {

	private final FilterService filterService;

	private final ObjectMapper objectMapper;
	public FilterTestController(FilterService clovaService, ObjectMapper objectMapper){
		this.filterService = clovaService;
		this.objectMapper = objectMapper;
	}
	@GetMapping("clova")
	public void clova(){
		List<CommonMessage> messages = new ArrayList<>();
		CommonMessage message1 = CommonMessage.builder()
				.senderId(UUID.randomUUID())
				.content("내용1").build();
		messages.add(message1);

		CommonMessage message2 = CommonMessage.builder()
			.senderId(UUID.randomUUID())
			.content("욕설1").build();
		messages.add(message2);

		filterService.token(messages, new FutureCallback<>() {
			@Override
			public void completed(SimpleHttpResponse simpleHttpResponse) {
				if (simpleHttpResponse.getCode()==200){
					try {
						Map<?, ?> map = objectMapper.readValue(simpleHttpResponse.getBody().getBodyText(), Map.class);
						// System.out.println(simpleHttpResponse.getBody().getBodyText());
						// System.out.println(map);
						if (((Map<?, ?>)map.get("status")).get("code").equals("20000")){
							List<?> messages = (List<?>)((Map<?, ?>)map.get("result")).get("messages");
							int count = (int)((Map<?,?>) messages.get(0)).get("count");
							System.out.println("count: "+count);
						}
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
		});
		filterService.predict(messages, new FutureCallback<>() {
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
							if (resultArr.length != messages.size()) throw new RuntimeException("clova predict failed");
							String[] resultTypeArr = new String[resultArr.length];
							for (int i=0; i<resultArr.length; i++){
								switch (resultArr[i]){
									case "욕설/비하/모욕":
										messages.get(i).setFilteredType(FilteredType.MALICIOUS_CLOVA.name());
										resultTypeArr[i] = FilteredType.MALICIOUS_CLOVA.name();
										break;
									case "성희롱/혐오":
										messages.get(i).setFilteredType(FilteredType.HATE_CLOVA.name());
										resultTypeArr[i] = FilteredType.HATE_CLOVA.name();
										break;
									case "도배/홍보":
										messages.get(i).setFilteredType(FilteredType.HATE_LAMBDA.name());
										resultTypeArr[i] = FilteredType.SPAM.name();
										break;
									case "중립":
										messages.get(i).setFilteredType(FilteredType.NOT_FILTERED.name());
										resultTypeArr[i] = FilteredType.NOT_FILTERED.name();
										break;
									default:
										throw new RuntimeException("clova predict failed");
								}
							}
							System.out.println(Arrays.toString(resultTypeArr));
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
		});
	}

	@GetMapping("lambda")
	public void lambda(){
		CommonMessage message = CommonMessage.builder()
			.senderId(UUID.randomUUID())
			.content("내용1").build();

		filterService.predict(message, new FutureCallback<>() {
			@Override
			public void completed(SimpleHttpResponse simpleHttpResponse) {
				if (simpleHttpResponse.getCode()==200){
					try {
						Map<?, ?> map = objectMapper.readValue(simpleHttpResponse.getBody().getBodyText(), Map.class);
						int value = (int)map.get("prediction");
						switch (value){
							case 0:
								message.setFilteredType(FilteredType.MALICIOUS_LAMBDA.name());
								break;
							case 1:
								message.setFilteredType(FilteredType.HATE_LAMBDA.name());
								break;
							case 2:
								message.setFilteredType(FilteredType.NOT_FILTERED.name());
								break;
							default:
								throw new RuntimeException("lambda predict api error");
						}
						System.out.println(message.getFilteredType());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

				} else throw new RuntimeException("lambda predict api error");
			}
			@Override
			public void failed(Exception e) {
				System.out.println(e);
			}
			@Override
			public void cancelled() {
				System.out.println("clova predict cancelled");
			}
		});
	}

	@GetMapping("bad")
	public void bad(){
		CommonMessage message = CommonMessage.builder()
			.senderId(UUID.randomUUID())
			.content("시발").build();
		if (filterService.isBadWord(message)){
			message.setFilteredType(FilteredType.MALICIOUS_SIMPLE.name());
		}
		else{
			message.setFilteredType(FilteredType.NOT_FILTERED.name());
		}

		System.out.println(message.getFilteredType());
	}
}
