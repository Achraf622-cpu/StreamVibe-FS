package com.streamvibe.authservice;

import com.streamvibe.authservice.client.AuthUserDTO;
import com.streamvibe.authservice.client.UserClient;
import com.streamvibe.authservice.dto.AuthRequest;
import com.streamvibe.authservice.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            // Fetch User Details remotely from User Service
            AuthUserDTO user = userClient.getAuthUserByUsername(request.getUsername());

            // Validate BCrypt Password
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtService.generateToken(user.getUsername());

                AuthResponse response = AuthResponse.builder()
                        .token(token)
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build();

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam String token) {
        jwtService.validateToken(token);
        return "Token is valid";
    }
}
