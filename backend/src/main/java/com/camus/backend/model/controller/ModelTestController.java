package com.camus.backend.model.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.model.service.ModelService;

@RestController
@RequestMapping("model")
public class ModelTestController {

	private final ModelService modelService;

	private final FutureCallback<SimpleHttpResponse> testCallback;
	public ModelTestController(ModelService clovaService){
		this.modelService = clovaService;
		this.testCallback = new FutureCallback<>() {
			@Override
			public void completed(SimpleHttpResponse simpleHttpResponse) {
				System.out.println(simpleHttpResponse.getCode());
				System.out.println(simpleHttpResponse.getBody().getBodyText());
			}
			@Override
			public void failed(Exception e) {
				System.out.println(e);
			}
			@Override
			public void cancelled() {
				System.out.println("cancelled");
			}
		};
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

		modelService.token(messages, testCallback);
		modelService.predict(messages, testCallback);
	}

	@GetMapping("lambda")
	public void lambda(){
		CommonMessage message = CommonMessage.builder()
			.senderId(UUID.randomUUID())
			.content("내용1").build();

		modelService.predict(message, testCallback);
	}
}
