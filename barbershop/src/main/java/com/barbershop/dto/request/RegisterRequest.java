package com.barbershop.dto.request;

import jakarta.validation.constraints.*;

public record RegisterRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String fullName,

        @NotBlank @Email(message = "Email inválido")
        String email,

        @NotBlank @Size(min = 6, message = "Mínimo 6 caracteres")
        String password,

        // Teléfono opcional al crear barbero desde el panel admin
        // Cuando viene del registro público se valida el patrón chileno
        String phone
) {}
