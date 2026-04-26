package org.saurav.controller;

import org.saurav.ServiceBusTopic;
import org.saurav.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ServiceBusTopicController {

    @Autowired
    private ServiceBusTopic topService;

    @PatchMapping("/createTopic/{topic}")
    public ResponseEntity<String> createTopic(@PathVariable String topic) {
        String status = topService.createTopic(topic);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/sendMsgToTopic")
    public ResponseEntity<String> sendMsg(@RequestParam String topic, @RequestHeader String msg){
        String status = topService.sendMessage(topic,msg);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/sendMsgsToTopic")
    public ResponseEntity<String> sendMsgs(@RequestParam String topic, @RequestBody List<String> msgList){
        String status = topService.sendMessages(topic,msgList);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/reciveMessage/{sub}")
    public ResponseEntity<List<String>> sendMsgs(@RequestParam String topic, @PathVariable String sub){
        List<String> msgList = topService.receiveMessages(topic,sub);
        return ResponseEntity.ok(msgList);
    }

    @GetMapping("/getMessage/{sub}")
    public ResponseEntity<List<String>> getMsg(@RequestParam String topic, @PathVariable String sub){
        List<String> msg = topService.receiveMessage(topic,sub);
        return ResponseEntity.ok(msg);
    }

    @PostMapping("/sendStudent")
    public ResponseEntity<String> getMsg(@RequestParam String topic, @RequestBody List<Student> studentList){
        String msg = topService.sendStudents(topic,studentList);
        return ResponseEntity.ok(msg);
    }
}
