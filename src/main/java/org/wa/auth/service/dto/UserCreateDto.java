package org.wa.auth.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Email
    @NotBlank
    private String email;

    @Size(min = 11, max = 11)
    @Pattern(regexp = "\\+?[0-9]{10,15}")
    private String phone;

    @NotBlank
    private String password;

    private StatusEnum status;

    private Set<RoleEnum> roles;
}
