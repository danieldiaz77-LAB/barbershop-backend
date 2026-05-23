package com.barbershop.service;

import com.barbershop.dto.request.LoginRequest;
import com.barbershop.dto.request.RegisterRequest;
import com.barbershop.dto.response.AuthResponse;
import com.barbershop.exception.BadRequestException;
import com.barbershop.model.User;
import com.barbershop.model.enums.Role;
import com.barbershop.repository.UserRepository;
import com.barbershop.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest req) {

        // verifica que el email no esté en uso
        if (userRepository.existsByEmail(req.email())) {
            throw new BadRequestException("El email ya está registrado");
        }

        // si no envía rol, se asigna CLIENT por defecto
        Role role = req.role() == null ? Role.CLIENT : req.role();

        User user = User.builder()
                .email(req.email().toLowerCase().trim())
                .password(passwordEncoder.encode(req.password()))
                .fullName(req.fullName())
                .phone(req.phone())
                .role(role)
                .build();

        user = userRepository.save(user);

        // genera el token con email como subject y rol/userId como claims
        String token = jwtService.generateToken(
                user.getEmail(),
                Map.of("role", user.getRole().name(),
                        "userId", user.getId().toString())
        );

        return new AuthResponse(
                token, user.getId(),
                user.getEmail(), user.getFullName(), user.getRole()
        );
    }

    public AuthResponse login(LoginRequest req) {

        // Spring Security valida email y password automáticamente
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.email().toLowerCase().trim(),
                        req.password()
                )
        );

        User user = userRepository.findByEmail(req.email().toLowerCase().trim())
                .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

        String token = jwtService.generateToken(
                user.getEmail(),
                Map.of("role", user.getRole().name(),
                        "userId", user.getId().toString())
        );

        return new AuthResponse(
                token, user.getId(),
                user.getEmail(), user.getFullName(), user.getRole()
        );
    }
}