package com.barbershop.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ServiceRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        String description,

        @NotNull @Min(value = 5, message = "Duración mínima 5 minutos")
        Integer durationMinutes,

        @NotNull @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
        BigDecimal price,

        // si no se envía, queda activo por defecto
        Boolean active
) {}