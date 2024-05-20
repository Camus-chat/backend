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
import com.camus.backend.filter.service.FilterService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Service
public class KafkaFilterConsumer {
	private final FilterService filterService;
	private final ObjectMapper objectMapper;
	public KafkaFilterConsumer(
		FilterService filterService,
		ObjectMapper objectMapper) {
		this.filterService = filterService;
		this.objectMapper = objectMapper;
	}


	@KafkaListener(topics = "FilteringRequest", groupId = "FilteringGroup")
	public void listen(ConsumerRecord<String, Object> record) {
		try {
			FilteringRequest request = objectMapper.readValue(record.value().toString(), FilteringRequest.class);

			if (request instanceof SingleFilteringRequest singleFilteringRequest) {
				filterService.predict(singleFilteringRequest);
			} else if (request instanceof ContextFilteringRequest contextFilteringRequest) {
				filterService.predict(contextFilteringRequest);
			} else {
				throw new RuntimeException("Unsupported request type");
			}
		} catch (Exception e) {
			e.printStackTrace(); // 오류 출력
		}
	}

}
