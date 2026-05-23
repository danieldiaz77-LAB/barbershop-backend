package com.barbershop.dto.response;

import com.barbershop.model.Service;
import java.math.BigDecimal;
import java.util.UUID;

public record ServiceResponse(
        UUID id,
        String name,
        String description,
        Integer durationMinutes,
        BigDecimal price,
        Boolean active
) {
    public static ServiceResponse from(Service s) {
        return new ServiceResponse(
                s.getId(), s.getName(), s.getDescription(),
                s.getDurationMinutes(), s.getPrice(), s.getActive()
        );
    }
}