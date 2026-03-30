package org.wa.auth.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wa.auth.service.dto.AdminUserBlockResponse;
import org.wa.auth.service.service.AdminService;

import java.util.UUID;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/block/{id}")
    public ResponseEntity<AdminUserBlockResponse> blockUser(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(adminService.blockUser(id));
    }

    @PostMapping("/unblock/{id}")
    public ResponseEntity<AdminUserBlockResponse> unblockUser(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(adminService.unblockUser(id));
    }
}
