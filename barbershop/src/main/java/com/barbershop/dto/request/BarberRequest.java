package com.barbershop.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BarberRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotBlank(message = "La especialidad es obligatoria")
        String specialty,

        String photoUrl,
        String bio,

        // formato "HH:mm" ej: "09:00"
        @NotBlank(message = "La hora de inicio es obligatoria")
        String workStart,

        @NotBlank(message = "La hora de fin es obligatoria")
        String workEnd
) {}