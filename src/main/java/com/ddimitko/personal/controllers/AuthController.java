package com.ddimitko.personal.controllers;

import com.ddimitko.personal.DTOs.AuthResponseDto;
import com.ddimitko.personal.DTOs.LoginDto;
import com.ddimitko.personal.DTOs.SignupDto;
import com.ddimitko.personal.services.AuthService;
import com.ddimitko.personal.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto){

        //01 - Receive the token from AuthService
        String token = authService.login(loginDto);

        //02 - Set the token as a response using JwtAuthResponse Dto Class
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(token);

        //03 - Return the response to the user
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupDto> newUser(@Valid @RequestBody SignupDto signupDto) throws Exception {
        if(userService.userExists(signupDto.getUserTag())){
            return ResponseEntity.badRequest().build();
        }
        userService.addUser(signupDto);
        return ResponseEntity.ok().build();
    }

}
