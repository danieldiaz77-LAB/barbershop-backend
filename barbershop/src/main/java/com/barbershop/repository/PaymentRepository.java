package com.barbershop.repository;

import com.barbershop.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // busca pago por ID de sesión de Stripe (para webhook y verificación)
    Optional<Payment> findByStripeSessionId(String sessionId);

    // busca pago por ID de cita
    Optional<Payment> findByAppointmentId(UUID appointmentId);
}