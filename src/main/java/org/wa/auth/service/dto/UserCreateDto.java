package org.wa.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wa.auth.service.model.RoleEnum;
import org.wa.auth.service.model.StatusEnum;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    private String email;
    private String phone;
    private String password;
    private StatusEnum status;
    private Set<RoleEnum> roles;
}
