package com.barbershop.service;

import com.barbershop.dto.request.AppointmentRequest;
import com.barbershop.dto.response.AppointmentResponse;
import com.barbershop.exception.BadRequestException;
import com.barbershop.exception.NotFoundException;
import com.barbershop.exception.SlotConflictException;
import com.barbershop.model.Appointment;
import com.barbershop.model.Barber;
import com.barbershop.model.User;
import com.barbershop.model.enums.AppointmentStatus;
import com.barbershop.model.enums.Role;
import com.barbershop.repository.AppointmentRepository;
import com.barbershop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final BarberService barberService;
    private final ServiceCatalogService serviceCatalogService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              BarberService barberService,
                              ServiceCatalogService serviceCatalogService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.barberService = barberService;
        this.serviceCatalogService = serviceCatalogService;
    }

    @Transactional
    public AppointmentResponse book(String clientEmail, AppointmentRequest req) {

        User client = userRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        Barber barber = barberService.findById(req.barberId());
        com.barbershop.model.Service service = serviceCatalogService.findById(req.serviceId());

        // no se puede agendar en el pasado
        if (!req.startTime().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("No se puede agendar en el pasado");
        }

        LocalDateTime endTime = req.startTime().plusMinutes(service.getDurationMinutes());

        // valida que la cita esté dentro del horario del barbero
        validateWorkingHours(barber, req.startTime(), endTime);

        // verifica que no haya solapamiento con otra cita activa
        List<Appointment> overlaps = appointmentRepository
                .findOverlapping(barber, req.startTime(), endTime);

        if (!overlaps.isEmpty()) {
            throw new SlotConflictException(
                    "El barbero no está disponible en ese horario, elige otro slot");
        }

        Appointment appointment = Appointment.builder()
                .client(client)
                .barber(barber)
                .service(service)
                .startTime(req.startTime())
                .endTime(endTime)
                .status(AppointmentStatus.PENDING)
                .build();

        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    // valida horario laboral y que no sea domingo
    private void validateWorkingHours(Barber barber,
                                      LocalDateTime start,
                                      LocalDateTime end) {
        if (start.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new BadRequestException("El local está cerrado los domingos");
        }

        LocalTime workStart = LocalTime.parse(barber.getWorkStart());
        LocalTime workEnd   = LocalTime.parse(barber.getWorkEnd());

        // la cita debe terminar el mismo día que empieza
        if (!start.toLocalDate().equals(end.toLocalDate())) {
            throw new BadRequestException("La cita debe terminar el mismo día");
        }

        if (start.toLocalTime().isBefore(workStart) ||
                end.toLocalTime().isAfter(workEnd)) {
            throw new BadRequestException(
                    "Horario fuera del rango del barbero (" +
                            barber.getWorkStart() + " - " + barber.getWorkEnd() + ")"
            );
        }
    }

    // retorna slots disponibles cada 30 minutos para un barbero/servicio/fecha
    public List<LocalDateTime> availableSlots(UUID barberId,
                                              UUID serviceId,
                                              LocalDate date) {
        Barber barber = barberService.findById(barberId);
        com.barbershop.model.Service service = serviceCatalogService.findById(serviceId);

        // domingos no hay slots
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) return List.of();

        LocalTime workStart = LocalTime.parse(barber.getWorkStart());
        LocalTime workEnd   = LocalTime.parse(barber.getWorkEnd());

        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime cursor  = LocalDateTime.of(date, workStart);
        LocalDateTime closeAt = LocalDateTime.of(date, workEnd);

        while (!cursor.plusMinutes(service.getDurationMinutes()).isAfter(closeAt)) {
            LocalDateTime slotEnd = cursor.plusMinutes(service.getDurationMinutes());
            boolean isPast     = cursor.isBefore(LocalDateTime.now());
            boolean hasConflict = !appointmentRepository
                    .findOverlapping(barber, cursor, slotEnd).isEmpty();

            if (!isPast && !hasConflict) slots.add(cursor);

            // avanza 30 minutos para el siguiente slot
            cursor = cursor.plusMinutes(30);
        }

        return slots;
    }

    // historial de citas del cliente autenticado
    public List<AppointmentResponse> myAppointments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return appointmentRepository.findByClientOrderByStartTimeDesc(user)
                .stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    // citas de un barbero (opcionalmente filtradas por fecha)
    public List<AppointmentResponse> barberAppointments(UUID barberId, LocalDate date) {
        Barber barber = barberService.findById(barberId);
        if (date != null) {
            return appointmentRepository
                    .findByBarberAndStartTimeBetweenOrderByStartTimeAsc(
                            barber,
                            date.atStartOfDay(),
                            date.plusDays(1).atStartOfDay()
                    )
                    .stream().map(AppointmentResponse::from).toList();
        }
        return appointmentRepository.findByBarberOrderByStartTimeDesc(barber)
                .stream().map(AppointmentResponse::from).toList();
    }

    @Transactional
    public AppointmentResponse cancel(UUID id, String requesterEmail) {
        Appointment appointment = findEntityById(id);

        // puede cancelar el propio cliente o un admin
        boolean isOwner = appointment.getClient().getEmail()
                .equalsIgnoreCase(requesterEmail);
        boolean isAdmin = userRepository.findByEmail(requesterEmail)
                .map(u -> u.getRole() == Role.BARBER_ADMIN)
                .orElse(false);

        if (!isOwner && !isAdmin) {
            throw new BadRequestException("No tienes permiso para cancelar esta cita");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse markPaid(UUID id) {
        Appointment appointment = findEntityById(id);
        appointment.setStatus(AppointmentStatus.PAID);
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    public AppointmentResponse getById(UUID id) {
        return AppointmentResponse.from(findEntityById(id));
    }

    private Appointment findEntityById(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cita no encontrada"));
    }
}