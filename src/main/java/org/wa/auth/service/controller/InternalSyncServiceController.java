package org.wa.auth.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wa.auth.service.service.UserService;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/internal/sync")
public class InternalSyncServiceController {

    private final UserService service;

    @GetMapping
    public ResponseEntity<Flux<Object>> getAllUsers() {
        return ResponseEntity.ok(service.streamAllUsers());
    }
}
