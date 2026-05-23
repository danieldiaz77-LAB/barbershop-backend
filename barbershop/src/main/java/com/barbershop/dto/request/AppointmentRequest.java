package com.barbershop.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequest(

        @NotNull(message = "El barbero es obligatorio")
        UUID barberId,

        @NotNull(message = "El servicio es obligatorio")
        UUID serviceId,

        // fecha y hora de inicio de la cita
        @NotNull(message = "La hora de inicio es obligatoria")
        LocalDateTime startTime
) {}