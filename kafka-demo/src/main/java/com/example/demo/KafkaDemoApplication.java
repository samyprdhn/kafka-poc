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
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class KafkaDemoApplication {

	private final static String TOPIC = "kafkademo";
	private final static String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

	// @Bean
	CommandLineRunner clr(KafkaTemplate<String, String> kafkaTemplate, HashMap<String, Object> producerProps) {
		return args -> {

			kafkaTemplate.send(TOPIC, "spring-kafka-" + RandomStringUtils.randomAlphanumeric(10));
			

			// System.out.println(producerProps);

		};
	}

	@Bean
	ApplicationRunner ar(KafkaTemplate< String, String> kafkaTemplate) {
		return args -> {
			try{
			for (int i = 0; i < 1; i++) {
				System.out.println("PUBLISHING");
				System.out.println(kafkaTemplate.send(TOPIC, "spring-kafka-" + RandomStringUtils.randomAlphanumeric(10)));
			}
			} catch (Exception e){
				e.printStackTrace();
			}

			// System.out.println(args);

		};
	}
	
	@Component
	
	public class Listener {
		
		@KafkaListener(topics = "kafkademo")
		@SendTo("test")
		public String listen(@Payload String message){
			System.out.println(message);
			return message;
		}
		
		
		
	}
	

	

	//
	// @Component
	// class Listener2 {
	//
	// int i = 0;
	//
	// @KafkaListener(topics = TOPIC)
	// public void listen(@Payload String message,
	// @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int offset, Acknowledgment
	// ack) throws Exception{
	// System.out.println("2Message Received: " + message);
	// System.out.println("2Offset Id : " + offset);
	// //System.out.println("2Partition Id : " + partition);
	// //Thread.sleep(200L);
	// i++;
	////
	//// if (i == 10) {
	//// System.out.println("message exception" + message);
	////
	////
	//// Thread.currentThread().interrupt();
	//// throw new Exception("STOP CONSUMER");
	//// }
	//
	// ack.acknowledge();
	// }
	//
	// }

	public static void main(String[] args) {
		SpringApplication.run(KafkaDemoApplication.class, args);
	}
}
