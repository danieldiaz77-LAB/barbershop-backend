package com.barbershop.dto.response;

import com.barbershop.model.Payment;
import com.barbershop.model.enums.PaymentMethod;
import com.barbershop.model.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID appointmentId,
        BigDecimal amount,
        String currency,
        PaymentMethod method,
        PaymentStatus status,
        String stripeSessionId,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getAppointment().getId(),
                p.getAmount(),
                p.getCurrency(),
                p.getMethod(),
                p.getStatus(),
                p.getStripeSessionId(),
                p.getPaidAt(),
                p.getCreatedAt()
        );
    }
}