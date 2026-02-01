package org.wa.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncServiceDto {
    private Long id;
    private String email;
    private String googleRefreshToken;
}
