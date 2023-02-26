package com.example.payment;


import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.math.BigDecimal;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties; //application.yml içindeki kafka değelerini okumamızı sağlayacak.

    private final PaymentRepository paymentRepository; //Gelen order eventini paymente işlem yapma

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);
        factory.setConcurrency(kafkaProperties.getListener().getConcurrency());
        factory.getContainerProperties().setAckMode(kafkaProperties.getListener().getAckMode());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, OrderMessage> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, OrderDeserializer.class); //Gelen Order mesajını nasıl deserialize edeceğimizi belirtiyoruz.
        return props;
    }

    @KafkaListener(topics = "mysql.orders.orders", groupId = "payments") //topic adımızı veiyoruz.groupId ise kafka ui daki consumer içinde hangi gruoup varsa.
    public void listenOrderEvents(OrderMessage message) {
        OrderMessage.After data = message.getPayload().getAfter(); //mesajı alıyoruz.
        switch (message.getPayload().getOp()) {
            case "c": //create
                System.out.println("Received create event");
                Payment payment = Payment.builder()
                        .orderId(data.getId())
                        .amount(new BigDecimal(data.getPrice()))
                        .build();
                paymentRepository.save(payment);
            case "u": //update
                System.out.println("Received update event");
                Payment payment1 = paymentRepository.findByOrderId(data.getId()).orElseThrow(()-> new RuntimeException("not found"));
                payment1.setAmount(new BigDecimal(data.getPrice()));
                paymentRepository.save(payment1);
        }
    }
}
/*
Bir Order oluşacak .Veri tabanında oluşacak.Debezium ile kafkaya yazılacak.Kafkayı dinleyen Payment da bu dataı okuyacak ve kendiş dbsine yazacak.
Ordan da data önce Pending , eğer Payment bu datayı alırsa kendini Success e alacak..
 */