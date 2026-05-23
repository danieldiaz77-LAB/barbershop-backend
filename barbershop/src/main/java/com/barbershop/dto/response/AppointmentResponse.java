package com.barbershop.dto.response;

import com.barbershop.model.Appointment;
import com.barbershop.model.enums.AppointmentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID clientId,
        String clientName,
        UUID barberId,
        String barberName,
        UUID serviceId,
        String serviceName,
        BigDecimal servicePrice,
        Integer durationMinutes,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        LocalDateTime createdAt
) {
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getClient().getId(),
                a.getClient().getFullName(),
                a.getBarber().getId(),
                a.getBarber().getName(),
                a.getService().getId(),
                a.getService().getName(),
                a.getService().getPrice(),
                a.getService().getDurationMinutes(),
                a.getStartTime(),
                a.getEndTime(),
                a.getStatus(),
                a.getCreatedAt()
        );
    }
}