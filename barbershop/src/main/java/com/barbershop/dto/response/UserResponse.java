package com.barbershop.dto.response;

import com.barbershop.model.enums.Role;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        Role role,
        boolean emailVerified,
        LocalDateTime createdAt
) {}
