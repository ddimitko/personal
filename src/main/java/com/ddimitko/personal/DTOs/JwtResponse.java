package com.ddimitko.personal.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {

    private String accessToken;
    private String refreshToken;
}
