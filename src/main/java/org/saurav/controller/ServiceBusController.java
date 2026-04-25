package org.saurav.controller;

import org.saurav.ServiceBusQueueApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ServiceBusController {

    @Autowired
    private ServiceBusQueueApp sbusQueue;

    @PostMapping("/sendMessage")
    public ResponseEntity<String> sendMessage(@RequestParam String qName, @RequestBody String message) {
        String status = sbusQueue.sendMessage(qName,message);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/sendMessageBatch")
    public ResponseEntity<String> sendMessageBatch(@RequestParam String qName, @RequestBody List<String> messages) {
        String status = sbusQueue.sendMessageBatch(qName,messages);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/getMessage")
    public ResponseEntity<String> getMessage(@RequestParam String qName) {
        String status = sbusQueue.peekMessage(qName);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/peekAllMessage")
    public ResponseEntity<List<String>> getAllMessage(@RequestParam String qName,
                                                @RequestParam(required = false) boolean isDead) {

        List<String> messages = null;
        if(isDead) {
            messages = sbusQueue.peekMessagesFrmDeadLetterQueue(qName);
        } else {
            messages = sbusQueue.peekMessages(qName);
        }
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/recieveMessage")
    public ResponseEntity<List<String>> recieveAllMessage(@RequestParam String qName,
                                                      @RequestParam(required = false) boolean isDead) {

        List<String> messages = null;
        messages = sbusQueue.recieveMessages(qName,isDead);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/processMessage")
    public ResponseEntity<List<String>> processAllMessage(@RequestParam String qName,
                                                          @RequestParam(required = false) boolean isDead) {

        List<String> messages = null;
        messages = sbusQueue.completeMessages(qName,isDead);
        return ResponseEntity.ok(messages);
    }



}
