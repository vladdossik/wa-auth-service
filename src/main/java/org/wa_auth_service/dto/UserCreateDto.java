package org.wa_auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wa_auth_service.model.Status;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    private String email;
    private String phone;
    private String password;
    private Status status;
    private Set<String> roles;
}
