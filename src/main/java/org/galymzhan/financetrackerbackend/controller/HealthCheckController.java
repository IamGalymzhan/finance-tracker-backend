package org.galymzhan.financetrackerbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/healthcheck")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<String> healthcheck() {
        return ResponseEntity.ok("Application is running...");
    }
}
