package com.barbershop.controller;

import com.barbershop.dto.request.AppointmentRequest;
import com.barbershop.dto.response.AppointmentResponse;
import com.barbershop.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "Agendamiento de citas")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // cliente autenticado agenda una cita
    @Operation(summary = "Agendar una cita")
    @PostMapping
    public AppointmentResponse book(@Valid @RequestBody AppointmentRequest req,
                                    Authentication auth) {
        return appointmentService.book(auth.getName(), req);
    }

    // retorna las citas del cliente autenticado
    @Operation(summary = "Mis citas — cliente autenticado")
    @GetMapping("/mine")
    public List<AppointmentResponse> mine(Authentication auth) {
        return appointmentService.myAppointments(auth.getName());
    }

    // citas de un barbero, con filtro opcional por fecha
    @Operation(summary = "Citas por barbero y fecha opcional")
    @GetMapping("/barber/{barberId}")
    public List<AppointmentResponse> byBarber(
            @PathVariable UUID barberId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.barberAppointments(barberId, date);
    }

    // slots disponibles para agendar
    @Operation(summary = "Slots disponibles por barbero/servicio/fecha")
    @GetMapping("/availability")
    public List<LocalDateTime> availability(
            @RequestParam UUID barberId,
            @RequestParam UUID serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.availableSlots(barberId, serviceId, date);
    }

    // cliente o admin puede cancelar
    @Operation(summary = "Cancelar una cita")
    @PostMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable UUID id,
                                      Authentication auth) {
        return appointmentService.cancel(id, auth.getName());
    }

    @Operation(summary = "Obtener cita por ID")
    @GetMapping("/{id}")
    public AppointmentResponse getById(@PathVariable UUID id) {
        return appointmentService.getById(id);
    }
}