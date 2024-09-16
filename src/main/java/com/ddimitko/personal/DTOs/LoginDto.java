package com.ddimitko.personal.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDto {

    @NotNull
    private String userTag;
    @NotNull
    private String password;

}
