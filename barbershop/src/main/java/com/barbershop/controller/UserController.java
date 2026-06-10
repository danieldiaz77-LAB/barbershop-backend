package com.barbershop.controller;

import com.barbershop.dto.request.RegisterRequest;
import com.barbershop.dto.response.UserResponse;
import com.barbershop.exception.BadRequestException;
import com.barbershop.exception.NotFoundException;
import com.barbershop.model.Barber;
import com.barbershop.model.User;
import com.barbershop.model.enums.Role;
import com.barbershop.repository.AppointmentRepository;
import com.barbershop.repository.BarberRepository;
import com.barbershop.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Gestión de usuarios — solo Admin")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentRepository appointmentRepository;
    private final BarberRepository barberRepository;

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AppointmentRepository appointmentRepository,
                          BarberRepository barberRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appointmentRepository = appointmentRepository;
        this.barberRepository = barberRepository;
    }

    @Operation(summary = "Listar todos los usuarios — solo Admin")
    @GetMapping
    @PreAuthorize("hasRole('BARBER_ADMIN')")
    public ResponseEntity<List<UserResponse>> listAll() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(u -> new UserResponse(
                        u.getId(),
                        u.getFullName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getRole(),
                        u.isEmailVerified(),
                        u.getCreatedAt()
                ))
                .toList();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Crear cuenta de barbero — solo Admin")
    @PostMapping("/barber")
    @PreAuthorize("hasRole('BARBER_ADMIN')")
    public ResponseEntity<UserResponse> createBarber(@RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.email().toLowerCase().trim())) {
            throw new BadRequestException("Ya existe una cuenta con ese email");
        }

        User barber = User.builder()
                .fullName(req.fullName().trim())
                .email(req.email().toLowerCase().trim())
                .password(passwordEncoder.encode(req.password()))
                .phone(req.phone() != null ? req.phone().trim() : "")
                .role(Role.BARBER)
                .emailVerified(true)
                .build();

        User saved = userRepository.save(barber);

        return ResponseEntity.ok(new UserResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getRole(),
                saved.isEmailVerified(),
                saved.getCreatedAt()
        ));
    }

    @Operation(summary = "Eliminar usuario — solo Admin")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BARBER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        // Proteger cuentas admin
        if (user.getRole() == Role.BARBER_ADMIN) {
            throw new BadRequestException("No se puede eliminar una cuenta de administrador");
        }

        // 1. Borrar citas donde el usuario es CLIENTE
        appointmentRepository.deleteAll(
                appointmentRepository.findByClientOrderByStartTimeDesc(user)
        );

        // 2. Si el usuario es BARBERO: borrar también las citas de su agenda
        //    (el barber profile tiene FK → user; si se borra user primero hay conflicto)
        if (user.getRole() == Role.BARBER) {
            Optional<Barber> barberOpt = barberRepository.findAll()
                    .stream()
                    .filter(b -> b.getUser() != null && b.getUser().getId().equals(user.getId()))
                    .findFirst();

            if (barberOpt.isPresent()) {
                Barber barber = barberOpt.get();
                // Borrar citas donde aparece como barbero
                appointmentRepository.deleteAll(
                        appointmentRepository.findByBarberOrderByStartTimeDesc(barber)
                );
                // Desvincular el user del barber antes de borrar el user
                barber.setUser(null);
                barberRepository.save(barber);
            }
        }

        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
}
