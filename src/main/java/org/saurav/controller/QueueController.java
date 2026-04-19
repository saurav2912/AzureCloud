package org.saurav.controller;

import org.saurav.StorageQueueApp;
import org.saurav.Student;
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

    @PostMapping("/students")
    public ResponseEntity<String> pushStudent(@RequestParam("qName") String queueName, @RequestBody List<Student> students) {
        String status = queue.sendStudent(queueName,students);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/getAllMessage")
    public ResponseEntity<List<String>> getQueueMessages(@RequestParam("qName") String queueName) {
        List<String> allMsg = queue.peekMessages(queueName);
        return ResponseEntity.ok(allMsg);
    }

    @GetMapping("/recieveAllMessage")
    public ResponseEntity<List<String>> recieveQueueMessages(@RequestParam("qName") String queueName) {
        List<String> allMsg = queue.recieveMessage(queueName);
        return ResponseEntity.ok(allMsg);
    }

    @GetMapping("/clearQueue")
    public ResponseEntity<String> clearQueue(@RequestParam("qName") String queueName) {
        String msg = queue.deleteAllMessages(queueName);
        return ResponseEntity.ok(msg);
    }

    @PutMapping("/updateStudent/{id}")
    public ResponseEntity<String> updateStudent(@RequestParam("qName") String queueName, @PathVariable String id) {
        String msg = queue.updateMessage(queueName,id);
        return ResponseEntity.ok(msg);
    }


}
