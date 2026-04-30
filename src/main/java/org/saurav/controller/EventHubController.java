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
    public ResponseEntity<String> ingestEvent(@RequestBody List<String> list) {
        String status = eventService.ingestStringEvents(list);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/ingest/students")
    public ResponseEntity<String> ingestStudents(@RequestBody List<Student> list) {
        String status = eventService.ingestStudentEvents(list);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/egress/{id}")
    public ResponseEntity<List<String>> processEvent(@PathVariable String id) {
        List<String> eventList = eventService.processEventByPartition(id);
        return ResponseEntity.ok(eventList);
    }

    @GetMapping("/egress")
    public ResponseEntity<String> processEvent() {
        String status = eventService.processEvents();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/stop/egress")
    public ResponseEntity<String> stopProcessEvent() {
        String status = eventService.stopEvents();
        return ResponseEntity.ok(status);
    }
}
