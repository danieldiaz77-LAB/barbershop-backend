package com.barbershop.dto.response;

import com.barbershop.model.Barber;
import java.util.UUID;

public record BarberResponse(
        UUID id,
        String name,
        String specialty,
        String photoUrl,
        String bio,
        String workStart,
        String workEnd
) {
    // convierte entidad a DTO en un solo lugar
    public static BarberResponse from(Barber b) {
        return new BarberResponse(
                b.getId(), b.getName(), b.getSpecialty(),
                b.getPhotoUrl(), b.getBio(),
                b.getWorkStart(), b.getWorkEnd()
        );
    }
}