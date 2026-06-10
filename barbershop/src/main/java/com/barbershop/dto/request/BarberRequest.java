package com.barbershop.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record BarberRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotBlank(message = "La especialidad es obligatoria")
        String specialty,

        String photoUrl,
        String bio,

        @NotBlank(message = "La hora de inicio es obligatoria")
        String workStart,

        @NotBlank(message = "La hora de fin es obligatoria")
        String workEnd,

        // UUID del usuario a vincular (opcional al editar, obligatorio al crear)
        UUID userId
) {}
