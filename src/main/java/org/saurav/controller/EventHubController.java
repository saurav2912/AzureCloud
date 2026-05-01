package org.saurav.controller;

import org.saurav.EventHubService;
import org.saurav.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventHubController {

    @Autowired
    private EventHubService eventService;


    @PostMapping("/ingest")
    public ResponseEntity<String> ingestEvent(@RequestBody List<String> list,@RequestHeader String hubName) {
        String status = eventService.ingestStringEvents(list,hubName);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/ingest/students")
    public ResponseEntity<String> ingestStudents(@RequestBody List<Student> list, @RequestHeader String hubName) {
        String status = eventService.ingestStudentEvents(list,hubName);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/egress/{id}")
    public ResponseEntity<List<String>> processEvent(@PathVariable String id,@RequestHeader String hubName,@RequestHeader String consGrp) {
        List<String> eventList = eventService.processEventByPartition(id,hubName,consGrp);
        return ResponseEntity.ok(eventList);
    }

    @GetMapping("/egress")
    public ResponseEntity<String> processEvent(@RequestHeader String hubName,@RequestHeader String consGrp) {
        String status = eventService.processEvents(hubName,consGrp);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/stop/egress")
    public ResponseEntity<String> stopProcessEvent(@RequestHeader String hubName,@RequestHeader String consGrp) {
        String status = eventService.stopEvents(hubName,consGrp);
        return ResponseEntity.ok(status);
    }
}
