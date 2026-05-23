package com.barbershop.model;

import com.barbershop.model.enums.PaymentMethod;
import com.barbershop.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // cada cita tiene máximo un pago
    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // ID de sesión de Stripe Checkout
    @Column(unique = true)
    private String stripeSessionId;

    // ID del PaymentIntent de Stripe (llega después de pagar)
    private String stripePaymentIntentId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // fecha en que se confirmó el pago
    private LocalDateTime paidAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = PaymentStatus.INITIATED;
    }
}