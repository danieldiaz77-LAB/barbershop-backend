package com.barbershop.dto.response;

import com.barbershop.model.enums.Role;
import java.util.UUID;

public record AuthResponse(
        String token,
        UUID userId,
        String email,
        String fullName,
        Role role
) {}
