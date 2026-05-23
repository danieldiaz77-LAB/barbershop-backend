package com.barbershop.controller;

import com.barbershop.dto.request.LoginRequest;
import com.barbershop.dto.request.RegisterRequest;
import com.barbershop.dto.response.AuthResponse;
import com.barbershop.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Registro y login de usuarios")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Registrar nuevo usuario")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @Operation(summary = "Login — retorna JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    // retorna info del usuario autenticado según el token
    @Operation(summary = "Info del usuario autenticado")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication auth) {
        return ResponseEntity.ok(Map.of(
                "email", auth.getName(),
                "authorities", auth.getAuthorities()
        ));
    }
}