package org.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.model.Status;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateUpdateDto {
    private String email;
    private String phone;
    private String password;
    private Status status;
    private Set<String> roles;
}
