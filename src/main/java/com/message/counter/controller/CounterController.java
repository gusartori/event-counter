package com.message.counter.controller;


import com.message.counter.model.Event;
import com.message.counter.service.ConsumerService;
import com.message.counter.service.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path="/v1/messages", produces = APPLICATION_JSON_VALUE)
public class CounterController {

    private ConsumerService consumerService;

    private ProducerService producerService;

    @Autowired
    public CounterController(ConsumerService consumerService, ProducerService producerService) {
        this.consumerService = consumerService;
        this.producerService = producerService;
    }

    @GetMapping(path = "/produced/count")
    public ResponseEntity<String> getNumberOfProducedMessages(){
        var counter = producerService.getProducedCounter();
        return ResponseEntity.ok(
                String.format(
                """
                {
                    "numberOfMessages": "%s"
                }
                """, counter)
        );
    }

    @GetMapping(path = "/consumed/count")
    public ResponseEntity<String> getNumberOfConsumedMessages(){
        var counter = consumerService.getConsumedCounter();
        return ResponseEntity.ok(
                String.format(
                        """
                        {
                            "numberOfMessages": "%s"
                        }
                        """, counter)
        );
    }

    @GetMapping(path = "/count")
    public ResponseEntity<String> getNumberOfMessages(){
        var producedCounter = producerService.getProducedCounter();
        var consumedCounter = consumerService.getConsumedCounter();
        return ResponseEntity.ok(
                String.format(
                        """
                        {
                            "producedMessages": "%s",
                            "consumedMessages": "%s"
                        }
                        """, producedCounter, consumedCounter)
        );
    }

    @PostMapping(path = "/produce")
    public ResponseEntity<String> produceMessage(@RequestBody Event event){
        var message = event.message();
        producerService.sendMessage(message);
        return ResponseEntity.ok(
                String.format(
                        """
                        {
                            "producedMessage": "%s"
                        }
                        """, message)
        );
    }
}
