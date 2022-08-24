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

    public static final String TOPIC = "payments";

    public static void main(String[] args) throws InterruptedException {

        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put(SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        Producer<String, Order> producer = new KafkaProducer<>(props);


        String key;
        Order value;
        ProducerRecord<String, Order> producerRecord;

        for(int i = 1; i <= 5; i++) {
            key = String.valueOf(i);
            value = Order.newBuilder()
                    .setUserId("1234")
                    .setNbOfItems(1001 * i)
                    .setTotalAmount(100 * i)
                    .build();

            producerRecord = new ProducerRecord<>(TOPIC, key, value);

            producer.send(producerRecord);

            sleep(1000);
        }

        producer.close();
    }
}
