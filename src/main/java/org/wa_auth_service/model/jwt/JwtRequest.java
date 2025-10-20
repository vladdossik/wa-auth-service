package org.wa_auth_service.model.jwt;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JwtRequest {

    private String login;
    private String password;
}
