package com.message.counter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ConsumerService {

    private AtomicInteger consumedCounter = new AtomicInteger(0);

    @KafkaListener(topics = "custom-messages")
    public void listenCustomMessages(String message) {
        consumedCounter.addAndGet(1);
        log.info("Received Message: {}", message);
    }

    public Integer getConsumedCounter(){
        return consumedCounter.get();
    }
}
