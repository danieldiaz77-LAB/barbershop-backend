package com.barbershop.dto.request;

import com.barbershop.model.enums.Role;
import jakarta.validation.constraints.*;

public record RegisterRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String fullName,

        @NotBlank @Email(message = "Email inválido")
        String email,

        @NotBlank @Size(min = 6, message = "Mínimo 6 caracteres")
        String password,

        String phone,

        // si no se envía, se asigna CLIENT por defecto
        Role role
) {}