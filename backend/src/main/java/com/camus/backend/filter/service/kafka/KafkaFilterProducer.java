package com.camus.backend.filter.service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.camus.backend.filter.domain.Request.FilteringRequest;
import com.camus.backend.filter.util.component.FilterConstants;

@Service
public class KafkaFilterProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final FilterConstants filterConstants;
	public KafkaFilterProducer(KafkaTemplate<String, Object> kafkaTemplate,
		FilterConstants filterConstants) {
		this.kafkaTemplate = kafkaTemplate;
		this.filterConstants = filterConstants;
	}

	public void sendMessage(FilteringRequest filteringRequest) {
		System.out.println(filterConstants.FILTERING_REQ_TOPIC + ": " + filteringRequest.getRoomId());
		kafkaTemplate.send(filterConstants.FILTERING_REQ_TOPIC, filteringRequest);
	}
}
