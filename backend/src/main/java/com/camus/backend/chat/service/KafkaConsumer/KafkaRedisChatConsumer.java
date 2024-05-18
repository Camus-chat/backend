package com.camus.backend.chat.service.KafkaConsumer;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaRedisChatConsumer {
	private final ConcurrentKafkaListenerContainerFactory<String, String> factory;
	private final StringRedisTemplate stringRedisTemplate;

	public KafkaRedisChatConsumer(ConcurrentKafkaListenerContainerFactory<String, String> factory,
		StringRedisTemplate stringRedisTemplate) {
		this.factory = factory;
		this.stringRedisTemplate = stringRedisTemplate;
	}

	private ConcurrentMessageListenerContainer<String, String> container;

	public void addListener(String topic, String groupId) {
		container = factory.createContainer(topic);
		container.getContainerProperties().setGroupId(groupId);
		// redis로 값을 보내는 로직
		container.setupMessageListener((MessageListener<String, String>)message -> {
			System.out.println("Received message for Redis : " + message);
			// redis에 값을 추가하는 로직
			stringRedisTemplate.opsForList().rightPush(topic, message.value());
		});
		container.start();
	}

	public void stopContainer() {
		if (container != null) {
			container.stop();
			System.out.println("Kafka listener container stopped.");
		}
	}
}
