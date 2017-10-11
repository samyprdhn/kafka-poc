package com.example.demo;

import java.util.HashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class KafkaDemoApplication {

	private final static String TOPIC = "kafkademo";
	private final static String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

	@Bean
	CommandLineRunner clr(KafkaTemplate<String, String> kafkaTemplate, HashMap<String, Object> producerProps) {
		return args -> {

			kafkaTemplate.send(TOPIC, "spring-kafka-" + RandomStringUtils.randomAlphanumeric(10));

			// System.out.println(producerProps);

		};
	}

	@Bean
	ApplicationRunner ar(KafkaTemplate<String, String> kafkaTemplate) {
		return args -> {
			for (int i = 0; i < 100; i++) {
				// System.out.println("PUBLISHING");
				String random = RandomStringUtils.randomAlphanumeric(10);

				System.out.println(
						random + " : " + kafkaTemplate.send(TOPIC, "spring-kafka-" + random).get().getRecordMetadata());
			}

			// System.out.println(args);

		};
	}

	@Component
	class Listener1 {

		int i = 0;

		@KafkaListener(id = "listener1", topics = "kafkademo")
		public void listen(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) String partition,
				@Header(KafkaHeaders.OFFSET) String offset, Acknowledgment ack) throws Exception {

			System.out.println();
			System.out.println(Thread.currentThread().getName() + ": 1Message Received: " + message);
			System.out.println(Thread.currentThread().getName() + ": 1Offset Id : " + offset);
			System.out.println(Thread.currentThread().getName() + ": 1Partition Id : " + partition);
			//Thread.sleep(2000L);
			i++;
			//
			// if (i == 10) {
			// System.out.println("message exception" + message);
			//
			//
			// Thread.currentThread().interrupt();
			// throw new Exception("STOP CONSUMER");
			// }

			ack.acknowledge();
			System.out.println("1:" + i);
		}

	}

	@Component
	class Listener2 {

		int i = 0;

		@KafkaListener(id = "listener2", topics = "kafkademo")
		public void listen(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) String partition,
				@Header(KafkaHeaders.OFFSET) String offset, Acknowledgment ack) throws Exception {
			System.out.println(Thread.currentThread().getName() + ": 2Message Received: " + message);
			System.out.println(Thread.currentThread().getName() + ": 2Offset Id : " + offset);
			System.out.println(Thread.currentThread().getName() + ": 2Partition Id : " + partition);
			//Thread.sleep(200L);
			
			i++;
			if (i % 2 == 0) {
				i++;
				System.out.println(Thread.currentThread().getName() + ": message exception : " + message);

				// Thread.currentThread().interrupt();
				throw new Exception("STOP CONSUMER");
			}

			ack.acknowledge();
			System.out.println("2:" + i);
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);
	}
}
