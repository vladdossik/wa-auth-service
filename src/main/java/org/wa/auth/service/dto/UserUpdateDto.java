package org.wa.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wa.auth.service.model.Status;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private String email;
    private String phone;
    private String password;
    private Status status;
    private Set<String> roles;
}
