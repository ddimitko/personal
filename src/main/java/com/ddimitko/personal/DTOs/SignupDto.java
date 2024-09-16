package com.ddimitko.personal.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupDto {

    @NotNull
    private String userTag;

    @NotNull(message = "Email should not be empty.")
    @Email(message = "Not a valid email address.")
    private String email;

    @NotNull(message = "Password should not be empty.")
    @Size(min = 8, message = "Password too short.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$")
    private String password;
}
