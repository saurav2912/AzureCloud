package org.saurav.controller;

import org.saurav.StorageQueueApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QueueController {

    @Autowired
    private StorageQueueApp queue;

    @PostMapping("/createQueue")
    public ResponseEntity<String> createQueue(@RequestParam("qName") String queueName) {
        String status = queue.createQueue(queueName);
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/deleteQueue")
    public ResponseEntity<String> deleteQueue(@RequestParam("qName") String queueName) {
        String status = queue.deleteQueue(queueName);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/sendMsg")
    public ResponseEntity<String> pushMessage(@RequestParam("qName") String queueName, @RequestBody List<String> messages) {
        String status = queue.sendmessage(queueName,messages);
        return ResponseEntity.ok(status);
    }

    @GetMapping
    public ResponseEntity<String> getQueueMessages() {
        return ResponseEntity.ok("");
    }
}
