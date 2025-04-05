package com.spacedlearning.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller to handle the root endpoint for health checks
 */
@RestController
@Slf4j
public class HomeController {

    /**
     * Handles the root endpoint which is used for health checks
     *
     * @return A simple response indicating the service is running
     */
    @GetMapping("/")
    public ResponseEntity<String> home() {
        log.debug("REST request to get home/health check");
        return ResponseEntity.ok("Spaced Learning API is running");
    }
}