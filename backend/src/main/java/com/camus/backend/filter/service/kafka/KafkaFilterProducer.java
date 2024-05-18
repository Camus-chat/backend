package com.camus.backend.filter.service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.camus.backend.filter.domain.Request.FilteringRequest;
import com.camus.backend.filter.domain.Response.ContextFilteringResponse;
import com.camus.backend.filter.domain.Response.FilteringResponse;
import com.camus.backend.filter.domain.Response.SingleFilteringResponse;
import com.camus.backend.filter.util.component.FilterConstants;

@Service
public class KafkaFilterProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final FilterConstants filterConstants;
	public KafkaFilterProducer(
		KafkaTemplate<String, Object> kafkaTemplate,
		FilterConstants filterConstants) {
		this.kafkaTemplate = kafkaTemplate;
		this.filterConstants = filterConstants;
	}

	public void sendRequest(FilteringRequest filteringRequest) {
		System.out.println(filterConstants.FILTERING_REQ_TOPIC + ": " + filteringRequest.getRoomId());
		kafkaTemplate.send(filterConstants.FILTERING_REQ_TOPIC, filteringRequest);
	}

	public void sendResponse(FilteringResponse filteringResponse) {
		if (filteringResponse instanceof SingleFilteringResponse singleFilteringResponse){
			kafkaTemplate.send(filterConstants.SINGLE_FILTERING_RES_TOPIC, singleFilteringResponse);
			System.out.println("singleRes: "+filteringResponse.getRoomId());
		} else if (filteringResponse instanceof ContextFilteringResponse contextFilteringResponse){
			kafkaTemplate.send(filterConstants.CONTEXT_FILTERING_RES_TOPIC, contextFilteringResponse);
			System.out.println("contextRes: "+filteringResponse.getRoomId());
		}

	}
}
