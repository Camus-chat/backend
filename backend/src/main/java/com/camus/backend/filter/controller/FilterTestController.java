package com.camus.backend.filter.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

@RestController
@RequestMapping("filter")
public class FilterTestController {

	private final FilterService filterService;
	private final KafkaFilterProducer kafkaFilterProducer;

	public FilterTestController(FilterService clovaService,
		KafkaFilterProducer kafkaFilterProducer){
		this.filterService = clovaService;
		this.kafkaFilterProducer = kafkaFilterProducer;
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

		kafkaFilterProducer.sendRequest(new ContextFilteringRequest(messages, FilteringLevel.HIGH));
		// filterService.token(new ContextFilteringRequest(messages, FilteringLevel.HIGH));
		// filterService.predict(new ContextFilteringRequest(messages, FilteringLevel.HIGH));
	}

	@GetMapping("lambda")
	public void lambda(){
		CommonMessage message = CommonMessage.builder()
			.senderId(UUID.randomUUID())
			.content("내용1").build();
		kafkaFilterProducer.sendRequest(new SingleFilteringRequest(message, FilteringLevel.HIGH));
		// filterService.predict(new SingleFilteringRequest(message, FilteringLevel.HIGH));
	}

}
