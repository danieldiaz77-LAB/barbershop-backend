package com.barbershop.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CheckoutRequest(

        @NotNull(message = "El ID de la cita es obligatorio")
        UUID appointmentId,

        // URL base del frontend para redirigir después del pago
        @NotNull
        String originUrl
) {}