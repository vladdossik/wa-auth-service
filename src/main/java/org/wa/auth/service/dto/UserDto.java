package org.wa.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wa.auth.service.model.RoleEnum;
import org.wa.auth.service.model.StatusEnum;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String email;
    private String phone;
    private StatusEnum status;
    private Set<RoleEnum> roles;
}
