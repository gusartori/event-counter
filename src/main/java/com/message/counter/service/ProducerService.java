package com.message.counter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ProducerService {

    private AtomicInteger producedCounter = new AtomicInteger(0);
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    public ProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message){
        kafkaTemplate.send("custom-messages",message);
        producedCounter.addAndGet(1);
    }

    public Integer getProducedCounter(){
        return producedCounter.get();
    }
}
