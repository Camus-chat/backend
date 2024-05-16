package com.camus.backend.filter.service.kafka;

import java.util.Map;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
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
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ConcurrentKafkaListenerContainerFactory<String, Object> factory;
	private final FilterService filterService;
	private final FilterConstants filterConstants;
	private final ObjectMapper objectMapper;
	public KafkaFilterConsumer(KafkaTemplate<String, Object> kafkaTemplate,
		ConcurrentKafkaListenerContainerFactory<String, Object> factory,
		FilterService filterService,
		FilterConstants filterConstants,
		ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.filterConstants = filterConstants;
		this.filterService = filterService;
		this.factory = factory;
		this.objectMapper = objectMapper;
	}

	@PostConstruct
	private void addListener() {
		ConcurrentMessageListenerContainer<String, Object> container =
			factory.createContainer(filterConstants.FILTERING_REQ_TOPIC);
		container.getContainerProperties().setGroupId(filterConstants.FILTERING_GROUP_ID);
		container.setupMessageListener((MessageListener<String, Object>)message -> {
			try {
				FilteringRequest request = objectMapper.readValue(message.value().toString(), FilteringRequest.class);
				System.out.println(11);
				if (request instanceof SingleFilteringRequest singleFilteringRequest){
					if (filterService.isBadWord(singleFilteringRequest)){
						System.out.println("consumer: "+ FilteredType.MALICIOUS_SIMPLE);
					} else if (singleFilteringRequest.getFilteringLevel().getValue() >
						FilteringLevel.LOW.getValue()){
						filterService.predict(singleFilteringRequest);
					}
				} else if (request instanceof ContextFilteringRequest cfr){
					filterService.predict(cfr);
				} else throw new RuntimeException();
			} catch (Exception e){
				e.printStackTrace();
			}
		});
		container.start();
	}

}
