package org.wa.auth.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wa.auth.service.service.AdminService;

import java.util.UUID;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable("id") UUID id) {
        adminService.blockUser(id);
        String message = "User " + id + " blocked";
        return ResponseEntity.ok(message);
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable("id") UUID id) {
        adminService.unblockUser(id);
        String message = "User " + id + " unblocked";
        return ResponseEntity.ok(message);
    }
}
