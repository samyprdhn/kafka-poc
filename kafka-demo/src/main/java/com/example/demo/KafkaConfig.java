package com.example.demo;

import java.util.HashMap;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer.AckMode;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableKafka
public class KafkaConfig {

	@Bean
	public ProducerFactory<String, String> producerFactory(HashMap<String, Object> producerProps) {
		System.out.println(producerProps);
		return new DefaultKafkaProducerFactory<>(producerProps);
	}
	
	
	
//	@Bean
//	@ConfigurationProperties(prefix = "ei")
//	public HashMap<String, Object> testProps() {
//		HashMap<String, Object> configProps = new HashMap<>();
//		return configProps;
//	}
//	
	
	@Bean
	//@ConfigurationProperties(prefix = "ei")
	public HashMap<String, Object> producerProps() {
		HashMap<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//		configProps.put(ProducerConfig.RETRIES_CONFIG, "10");
//		configProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "500");
//		configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "500");
//		configProps.put("controlled.shutdown.retry.backoff.ms", "100000");
		return configProps;
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
		return new KafkaTemplate<>(producerFactory);
	}

	@Bean
	public HashMap<String, Object> consumerProps() {
		HashMap<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		
		return props;
	}

	@Bean
	public ConsumerFactory<String, String> consumerFactory(HashMap<String, Object> consumerProps) {
		return new DefaultKafkaConsumerFactory<>(consumerProps);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory) {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		factory.setRetryTemplate(new RetryTemplate());
		factory.setConcurrency(1);
		factory.getContainerProperties().setAckMode(AckMode.MANUAL);
		factory.getContainerProperties().setAckCount(1);
		factory.getContainerProperties().setAckOnError(false);
		
		return factory;
	}
	
	
//	@Component
//	class Listener {
//
//		@KafkaListener(topics = "kafkademo")
//		public void listen() {
//			//System.out.println("1Message Received: " + message);
//			// System.out.println("1Partition Id : " + partition);
//			
//			System.out.println("ola");
//			
//
//		}
//
//	}
	
}
