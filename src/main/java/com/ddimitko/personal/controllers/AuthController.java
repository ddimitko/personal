package com.ddimitko.personal.controllers;

import com.ddimitko.personal.DTOs.JwtResponse;
import com.ddimitko.personal.DTOs.LoginDto;
import com.ddimitko.personal.DTOs.SignupDto;
import com.ddimitko.personal.services.AuthService;
import com.ddimitko.personal.tokens.TokenRefreshRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        try {
            JwtResponse jwtResponse = authService.login(loginDto.getUserTag(), loginDto.getPassword(), request);
            return ResponseEntity.ok(jwtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignupDto signUpDto) {
        authService.signUp(signUpDto);
        return ResponseEntity.ok("User registered successfully.");
    }

    // POST /refresh-token
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest refreshRequest) {
        JwtResponse jwtResponse = authService.refreshAccessToken(refreshRequest.getRefreshToken());
        return ResponseEntity.ok(jwtResponse);
    }

    // POST /logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        // Extract the token from the Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring(7); // Remove "Bearer " prefix
            return authService.logout(accessToken);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
    }
}
