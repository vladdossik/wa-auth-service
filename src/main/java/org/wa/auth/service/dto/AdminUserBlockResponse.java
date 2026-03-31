package org.wa.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserBlockResponse {
    private UUID userId;
    private AdminBlockAction status;
}
