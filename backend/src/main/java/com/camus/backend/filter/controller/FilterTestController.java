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
import com.camus.backend.filter.domain.Request.ContextFilteringRequest;
import com.camus.backend.filter.domain.Request.SingleFilteringRequest;
import com.camus.backend.filter.service.FilterService;
import com.camus.backend.filter.service.kafka.KafkaFilterProducer;
import com.camus.backend.filter.util.type.FilteredType;
import com.camus.backend.filter.util.type.FilteringLevel;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/filter")
public class FilterTestController {

	private final FilterService filterService;
	private final KafkaFilterProducer kafkaFilterProducer;

	public FilterTestController(FilterService clovaService,
		KafkaFilterProducer kafkaFilterProducer){
		this.filterService = clovaService;
		this.kafkaFilterProducer = kafkaFilterProducer;
	}
	@GetMapping("/clova")
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

		kafkaFilterProducer.sendMessage(new ContextFilteringRequest(messages, FilteringLevel.HIGH));
		// filterService.token(new ContextFilteringRequest(messages, FilteringLevel.HIGH));
		// filterService.predict(new ContextFilteringRequest(messages, FilteringLevel.HIGH));
	}

	@GetMapping("/lambda")
	public void lambda(){
		CommonMessage message = CommonMessage.builder()
			.senderId(UUID.randomUUID())
			.content("내용1").build();
		kafkaFilterProducer.sendMessage(new SingleFilteringRequest(message, FilteringLevel.HIGH));
		// filterService.predict(new SingleFilteringRequest(message, FilteringLevel.HIGH));
	}

	@GetMapping("/bad")
	public void bad(){
		CommonMessage message = CommonMessage.builder()
			.senderId(UUID.randomUUID())
			.content("시발").build();
		if (filterService.isBadWord(new SingleFilteringRequest(message, FilteringLevel.HIGH))){
			message.setFilteredType(FilteredType.MALICIOUS_SIMPLE.name());
		}
		else{
			message.setFilteredType(FilteredType.NOT_FILTERED.name());
		}

		System.out.println(message.getFilteredType());
	}
}
