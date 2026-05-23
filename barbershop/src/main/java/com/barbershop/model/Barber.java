package com.barbershop.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "barbers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Barber {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    // especialidad del barbero (ej: Fades, Hot Towel Shave)
    @Column(nullable = false)
    private String specialty;

    @Column(columnDefinition = "TEXT")
    private String photoUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    // hora de inicio de trabajo (ej: "09:00")
    @Column(nullable = false)
    private String workStart;

    // hora de fin de trabajo (ej: "19:00")
    @Column(nullable = false)
    private String workEnd;

    // relación opcional con un usuario BARBER_ADMIN
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}