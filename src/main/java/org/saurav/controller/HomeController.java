package org.saurav.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/home")
    public ResponseEntity<String> getHome() {
        String text = "The service is up & Running";
        return ResponseEntity.ok(text);
    }
}
