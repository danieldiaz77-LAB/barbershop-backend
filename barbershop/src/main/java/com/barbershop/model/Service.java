package com.barbershop.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "services")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // duración en minutos
    @Column(nullable = false)
    private Integer durationMinutes;

    // precio con 2 decimales
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // si está activo en el catálogo
    @Column(nullable = false)
    private Boolean active = true;
}