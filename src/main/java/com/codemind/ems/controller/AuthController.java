package com.codemind.ems.controller;

import com.codemind.ems.dto.AuthRequest;
import com.codemind.ems.dto.AuthResponse;
import com.codemind.ems.dto.RegisterRequest;
import com.codemind.ems.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String message = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponse("Invalid token format"));
        }

        String token = authHeader.substring(7);
        AuthResponse response = authService.refreshToken(token);
        return ResponseEntity.ok(response);
    }
}
