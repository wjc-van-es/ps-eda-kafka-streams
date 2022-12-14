package com.pluralsight.kafka.streams;

import com.pluralsight.kafka.streams.model.Order;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static java.lang.Thread.sleep;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class Main {

    private static final String TOPIC = "payments";

    public static void main(String[] args) throws InterruptedException {

        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put(SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        Producer<String, Order> producer = new KafkaProducer<>(props);

        produceMessage(producer, "1",
                Order.newBuilder()
                        .setUserId("")
                        .setNbOfItems(5)
                        .setTotalAmount(5)
                        .build());

        for(int i = 1; i <= 5; i++) {
            produceMessage(producer, "2",
                    Order.newBuilder()
                            .setUserId("123")
                            .setNbOfItems(1001)
                            .setTotalAmount(100)
                            .build());
        }

        produceMessage(producer, "3",
                Order.newBuilder()
                        .setUserId("ghi")
                        .setNbOfItems(1)
                        .setTotalAmount(10001)
                        .build());

        produceMessage(producer, "4",
                Order.newBuilder()
                        .setUserId("abc")
                        .setNbOfItems(10)
                        .setTotalAmount(100)
                        .build());

        produceMessage(producer, "5",
                Order.newBuilder()
                        .setUserId("JKL")
                        .setNbOfItems(1)
                        .setTotalAmount(1)
                        .build());

        producer.close();
    }

    private static void produceMessage(Producer<String, Order> producer, String key, Order value) throws InterruptedException {
        ProducerRecord<String, Order> producerRecord = new ProducerRecord<>(TOPIC, key, value);

        producer.send(producerRecord);

        sleep(2000);
    }
}
