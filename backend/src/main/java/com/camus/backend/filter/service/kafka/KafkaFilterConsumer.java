package com.camus.backend.filter.service.kafka;

import java.util.Map;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

import com.camus.backend.filter.domain.Request.ContextFilteringRequest;
import com.camus.backend.filter.domain.Request.FilteringRequest;
import com.camus.backend.filter.domain.Request.SingleFilteringRequest;
import com.camus.backend.filter.domain.Response.SingleFilteringResponse;
import com.camus.backend.filter.service.FilterService;
import com.camus.backend.filter.util.component.FilterConstants;
import com.camus.backend.filter.util.component.FilterModule;
import com.camus.backend.filter.util.type.FilteredType;
import com.camus.backend.filter.util.type.FilteringLevel;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Service
public class KafkaFilterConsumer {
	private final ConcurrentKafkaListenerContainerFactory<String, Object> factory;
	private final FilterService filterService;
	private final FilterConstants filterConstants;
	private final ObjectMapper objectMapper;
	public KafkaFilterConsumer(
		ConcurrentKafkaListenerContainerFactory<String, Object> factory,
		FilterService filterService,
		FilterConstants filterConstants,
		ObjectMapper objectMapper) {
		this.filterConstants = filterConstants;
		this.filterService = filterService;
		this.factory = factory;
		this.objectMapper = objectMapper;
	}

	@KafkaListener(topics = "filteringRequest", groupId = "filteringGroup")
	public void listen(ConsumerRecord<String, Object> record) {
		try {
			FilteringRequest request = objectMapper.readValue(record.value().toString(), FilteringRequest.class);

			if (request instanceof SingleFilteringRequest) {
				filterService.predict((SingleFilteringRequest) request);
			} else if (request instanceof ContextFilteringRequest) {
				filterService.token((ContextFilteringRequest) request);
				filterService.predict((ContextFilteringRequest) request);
			} else {
				throw new RuntimeException("Unsupported request type");
			}
		} catch (Exception e) {
			e.printStackTrace(); // 오류 출력
		}
	}

}
