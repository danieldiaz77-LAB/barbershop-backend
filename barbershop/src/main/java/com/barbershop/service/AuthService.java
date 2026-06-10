package com.barbershop.service;

import com.barbershop.dto.request.LoginRequest;
import com.barbershop.dto.request.RegisterRequest;
import com.barbershop.dto.response.AuthResponse;
import com.barbershop.exception.BadRequestException;
import com.barbershop.model.User;
import com.barbershop.model.enums.Role;
import com.barbershop.repository.UserRepository;
import com.barbershop.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    private static final Set<String> BLOCKED_EMAIL_DOMAINS = Set.of(
            "mailinator.com",
            "10minutemail.com",
            "guerrillamail.com",
            "tempmail.com",
            "temp-mail.org",
            "yopmail.com",
            "trashmail.com"
    );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${app.api.url:http://localhost:8080}")
    private String apiUrl;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public AuthResponse register(RegisterRequest req) {
        String email = normalizeEmail(req.email());
        validateEmailDomain(email);

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("El email ya esta registrado");
        }

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(req.password()))
                .fullName(req.fullName().trim())
                .phone(req.phone() != null ? req.phone().trim() : "")
                .role(Role.CLIENT)
                .emailVerified(false)
                .emailVerificationToken(verificationToken)
                .emailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24))
                .build();

        user = userRepository.save(user);
        emailService.enviarVerificacionCliente(user, buildVerificationUrl(verificationToken));

        return toAuthResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        String email = normalizeEmail(req.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, req.password())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Credenciales invalidas"));

        return toAuthResponse(user);
    }

    public Map<String, String> verifyEmail(String token) {
        if (token == null || token.isBlank()) {
            throw new BadRequestException("Token de verificacion invalido");
        }

        User user = userRepository.findByEmailVerificationToken(token.trim())
                .orElseThrow(() -> new BadRequestException("Token de verificacion invalido"));

        if (user.getEmailVerificationTokenExpiresAt() == null ||
                user.getEmailVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El link de verificacion expiro. Inicia sesion y solicita uno nuevo.");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);
        userRepository.save(user);

        return Map.of("message", "Email verificado correctamente. Ya puedes reservar.");
    }

    public Map<String, String> resendVerification(String email) {
        User user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        if (user.isEmailVerified()) {
            return Map.of("message", "Este email ya esta verificado.");
        }

        String verificationToken = UUID.randomUUID().toString();
        user.setEmailVerificationToken(verificationToken);
        user.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        emailService.enviarVerificacionCliente(user, buildVerificationUrl(verificationToken));
        return Map.of("message", "Te enviamos un nuevo email de verificacion.");
    }

    private AuthResponse toAuthResponse(User user) {
        String token = jwtService.generateToken(
                user.getEmail(),
                Map.of("role", user.getRole().name(),
                        "userId", user.getId().toString(),
                        "emailVerified", user.isEmailVerified())
        );

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.isEmailVerified()
        );
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase().trim();
    }

    private void validateEmailDomain(String email) {
        if (email == null || !email.contains("@")) {
            throw new BadRequestException("Email invalido");
        }
        String domain = email.substring(email.lastIndexOf('@') + 1);
        if (BLOCKED_EMAIL_DOMAINS.contains(domain)) {
            throw new BadRequestException("Usa un email real para poder verificar tu cuenta.");
        }
    }

    private String buildVerificationUrl(String token) {
        return frontendUrl.replaceAll("/+$", "") + "/verificar-email?token=" + token;
    }
}
