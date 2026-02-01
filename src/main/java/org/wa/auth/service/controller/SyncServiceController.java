package org.wa.auth.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wa.auth.service.dto.SyncServiceDto;
import org.wa.auth.service.service.UserService;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/sync")
public class SyncServiceController {

    private final UserService service;

    @GetMapping
    public ResponseEntity<Map<Long, SyncServiceDto>> getAllUsers() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{email}")
    public ResponseEntity<SyncServiceDto> getUserByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(service.getUserByEmail(email));
    }
}
