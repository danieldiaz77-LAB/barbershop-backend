package com.barbershop.model;

import com.barbershop.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // nombre completo del usuario
    @Column(nullable = false)
    private String fullName;

    private String phone;

    // rol: CLIENT o BARBER_ADMIN
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // se asigna automáticamente al crear
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}