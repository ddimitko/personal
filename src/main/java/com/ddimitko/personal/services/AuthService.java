package com.ddimitko.personal.services;

import com.ddimitko.personal.DTOs.JwtResponse;
import com.ddimitko.personal.DTOs.SignupDto;
import com.ddimitko.personal.models.User;
import com.ddimitko.personal.repositories.UserRepository;
import com.ddimitko.personal.tools.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public AuthService(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, RedisTemplate<String, String> redisTemplate, UserDetailsService userDetailsService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.redisTemplate = redisTemplate;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    // Login method that authenticates user and generates tokens
    public JwtResponse login(String userTag, String password) {
        // Check if the user is already logged in by checking Redis for the access token
        String storedAccessToken = redisTemplate.opsForValue().get(userTag + "_access");
        if (storedAccessToken != null && jwtTokenUtil.validateToken(storedAccessToken)) {
            throw new RuntimeException("User is already logged in. Please log out first.");
        }

        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userTag, password));

        // Generate new tokens
        String accessToken = jwtTokenUtil.generateAccessToken(userTag);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userTag);

        // Store the tokens in Redis
        redisTemplate.opsForValue().set(userTag + "_access", accessToken);  // Store access token
        redisTemplate.opsForValue().set(userTag + "_refresh", refreshToken); // Store refresh token

        // Store the access token with a TTL equivalent to its expiration time
        long accessTokenExpirationMillis = jwtTokenUtil.getExpirationDateFromToken(accessToken).getTime() - System.currentTimeMillis();
        redisTemplate.opsForValue().set(userTag + "_access", accessToken, accessTokenExpirationMillis, TimeUnit.MILLISECONDS);

        // Store the refresh token with a TTL equivalent to its expiration time
        long refreshTokenExpirationMillis = jwtTokenUtil.getExpirationDateFromToken(refreshToken).getTime() - System.currentTimeMillis();
        redisTemplate.opsForValue().set(userTag + "_refresh", refreshToken, refreshTokenExpirationMillis, TimeUnit.MILLISECONDS);

        return new JwtResponse(accessToken, refreshToken);
    }

    public void signUp(SignupDto signUpDto) {
        // Check if userTag or email already exists
        if (userRepository.existsByUserTag(signUpDto.getUserTag())) {
            throw new RuntimeException("UserTag already taken!");
        }

        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        // Create a new User object
        User user = new User();
        user.setUserTag(signUpDto.getUserTag());
        user.setEmail(signUpDto.getEmail());

        // Encode the password
        String encodedPassword = bCryptPasswordEncoder.encode(signUpDto.getPassword());
        user.setPassword(encodedPassword);

        // Save the user in the database
        userRepository.save(user);
    }

    // Refresh Token method
    public JwtResponse refreshAccessToken(String refreshToken) {
        String userTag = jwtTokenUtil.getUserTagFromToken(refreshToken);

        // Check if the refresh token is still valid
        if (jwtTokenUtil.validateToken(refreshToken)) {
            // Generate a new access token
            String newAccessToken = jwtTokenUtil.generateAccessToken(userTag);

            // Update the access token in Redis
            redisTemplate.opsForValue().set(userTag + "_access", newAccessToken);

            return new JwtResponse(newAccessToken, refreshToken);
        } else {
            throw new RuntimeException("Invalid or expired refresh token");
        }
    }

    // Logout method that invalidates access token and removes refresh token from Redis
    public ResponseEntity<?> logout(String accessToken) {
        // Validate the access token
        if (jwtTokenUtil.validateToken(accessToken)) {

            String userTag = jwtTokenUtil.getUserTagFromToken(accessToken);
            // Remove tokens from Redis
            redisTemplate.delete(userTag + "_access");
            redisTemplate.delete(userTag + "_refresh");

            // Blacklist the access token
            long accessTokenExpirationMillis = jwtTokenUtil.getExpirationDateFromToken(accessToken).getTime() - System.currentTimeMillis();
            redisTemplate.opsForValue().set("blacklist_" + accessToken, "blacklisted", accessTokenExpirationMillis, TimeUnit.MILLISECONDS);

            // Clear SecurityContext
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok("Logged out successfully");

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    // Method to load user details using UserDetailsService
    public UserDetails loadUserByUserTag(String userTag) {
        return userDetailsService.loadUserByUsername(userTag);
    }
}


