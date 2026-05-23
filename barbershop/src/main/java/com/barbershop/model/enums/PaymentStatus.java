package com.barbershop.model.enums;

public enum PaymentStatus {
    INITIATED,  // sesión de pago creada
    PAID,       // pago confirmado
    FAILED,     // pago fallido
    EXPIRED,    // sesión expiró
    REFUNDED    // reembolsado
}