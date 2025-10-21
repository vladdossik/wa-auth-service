package org.wa.auth.service.model.jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshJwtRequest {
    public String refreshToken;
}
