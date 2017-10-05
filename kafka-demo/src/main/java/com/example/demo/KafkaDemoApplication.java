package com.example.demo;

import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.AbstractKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class KafkaDemoApplication {
	
	private final static String TOPIC = "kafkademo";
	private final static String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

	 //@Bean
	public CommandLineRunner producer() {
		return args -> {

			Properties props = new Properties();
			props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
			props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
			props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
			props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
			KafkaProducer<Long, String> producer = new KafkaProducer<>(props);

			for (int i = 0; i < 10; i++) {
				long startTime = System.currentTimeMillis();

				ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, 1L, "Back to the future part - " + i);

				producer.send(record, (metadata, exception) -> {
					long elapsedTime = System.currentTimeMillis() - startTime;
					if (metadata != null) {
						System.out.printf("native kafka sent record : (key=%s value=%s) " + "meta(partition=%d, offset=%d) time=%d\n",
								record.key(), record.value(), metadata.partition(), metadata.offset(), elapsedTime);
					} else {
						exception.printStackTrace();
					}
				});

			}

			producer.flush();
			producer.close();

			// System.out.println(producer.send(new ProducerRecord<Long,
			// String>(TOPIC, "SammyTesting"), (metadata, exception) -> ));

		};
	}

	// @Bean
	public CommandLineRunner consumer() {
		return args -> {

			Properties props = new Properties();
			props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
			props.put("group.id", "test");
			props.put("enable.auto.commit", "true");
			props.put("auto.commit.interval.ms", "1000");
			props.put("session.timeout.ms", "30000");
			props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

			KafkaConsumer<Long, String> consumer = new KafkaConsumer<>(props);

			consumer.subscribe(Arrays.asList(TOPIC));
			// consumer.beginningOffsets(Collections.singleton(new
			// TopicPartition(TOPIC, 1)));

			while (true) {
				ConsumerRecords<Long, String> records = consumer.poll(100);

				records.forEach(System.out::println);

			}

		}
		
		;
	}
	
	@Bean
	CommandLineRunner clr(KafkaTemplate<String, String> kafkaTemplate){
		return args -> {
			
			kafkaTemplate.send(TOPIC, "spring-kafka-" + RandomStringUtils.randomAlphanumeric(10));
			
		};
	}
	
//	
//	class CustomerListener extends AbstractKafkaListenerContainerFactory {
//
//		@Override
//		protected AbstractMessageListenerContainer createContainerInstance(KafkaListenerEndpoint endpoint) {
//
//			endpoint.
//			return null;
//		}
//		
//	}
	
	@Component
	class Listener {
		
		@KafkaListener(topics = TOPIC)
		public void listen(@Payload String message, 
				  @Header(KafkaHeaders.OFFSET) int offset){
			System.out.println("Message Received: " + message);
			System.out.println("Offset Id : " + offset);
			
			if(offset == 3)
			{
				
				System.out.println("SUCCESS");
				
			}			
		}
		
	}

	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);
	}
}
