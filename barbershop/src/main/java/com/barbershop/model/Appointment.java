package com.barbershop.model;

import com.barbershop.model.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments", indexes = {
        // índice para buscar citas por barbero y hora rápido
        @Index(name = "idx_appt_barber_start", columnList = "barber_id, startTime")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // cliente que agenda
    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private User client;

    // barbero asignado
    @ManyToOne(optional = false)
    @JoinColumn(name = "barber_id")
    private Barber barber;

    // servicio seleccionado
    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = AppointmentStatus.PENDING;
    }
}
