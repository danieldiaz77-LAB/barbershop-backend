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
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AppointmentService {

    private static final LocalTime WEEKDAY_OPEN  = LocalTime.of(10, 45);
    private static final LocalTime SATURDAY_OPEN = LocalTime.of(10, 0);
    private static final LocalTime CLOSE         = LocalTime.of(21, 0);
    private static final LocalTime LUNCH_START   = LocalTime.of(16, 0);
    private static final LocalTime LUNCH_END     = LocalTime.of(17, 0);
    private static final int MAX_ACTIVE_FUTURE_APPOINTMENTS = 5;
    private static final Pattern CHILE_PHONE = Pattern.compile("^\\+?56\\s?9\\s?\\d{4}\\s?\\d{4}$");
    private static final Set<String> BLOCKED_EMAIL_DOMAINS = Set.of(
            "mailinator.com",
            "10minutemail.com",
            "guerrillamail.com",
            "tempmail.com",
            "temp-mail.org",
            "yopmail.com",
            "trashmail.com"
    );

    private final AppointmentRepository  appointmentRepository;
    private final UserRepository         userRepository;
    private final BarberService          barberService;
    private final ServiceCatalogService  serviceCatalogService;
    private final EmailService           emailService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              BarberService barberService,
                              ServiceCatalogService serviceCatalogService,
                              EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository        = userRepository;
        this.barberService         = barberService;
        this.serviceCatalogService = serviceCatalogService;
        this.emailService          = emailService;
    }

    @Transactional
    public AppointmentResponse book(String clientEmail, AppointmentRequest req) {
        User client = userRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        validateClientCanBook(client);

        Barber barber = barberService.findById(req.barberId());
        com.barbershop.model.Service service = serviceCatalogService.findById(req.serviceId());

        if (!req.startTime().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("No se puede agendar en el pasado");
        }

        LocalDateTime endTime = req.startTime().plusMinutes(service.getDurationMinutes());
        validateWorkingHours(req.startTime(), endTime, barber);

        List<Appointment> overlaps = appointmentRepository.findOverlapping(barber, req.startTime(), endTime);
        if (!overlaps.isEmpty()) {
            throw new SlotConflictException("El barbero no esta disponible en ese horario, elige otro slot");
        }

        Appointment appointment = Appointment.builder()
                .client(client)
                .barber(barber)
                .service(service)
                .startTime(req.startTime())
                .endTime(endTime)
                .status(AppointmentStatus.PENDING)
                .notes(req.notes())
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        emailService.enviarConfirmacionCliente(saved);
        emailService.enviarNuevaCitaHoy(saved);

        return AppointmentResponse.from(saved);
    }

    public List<LocalDateTime> availableSlots(UUID barberId, UUID serviceId, LocalDate date) {
        Barber barber = barberService.findById(barberId);
        com.barbershop.model.Service service = serviceCatalogService.findById(serviceId);

        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) return List.of();

        LocalTime barberStart = parseTimeOrDefault(barber.getWorkStart(), businessStart(date));
        LocalTime barberEnd   = parseTimeOrDefault(barber.getWorkEnd(), CLOSE);

        List<LocalDateTime> slots    = new ArrayList<>();
        LocalDateTime       cursor   = LocalDateTime.of(date, barberStart);
        LocalDateTime       closeAt  = LocalDateTime.of(date, barberEnd);

        while (!cursor.plusMinutes(service.getDurationMinutes()).isAfter(closeAt)) {
            LocalDateTime slotEnd     = cursor.plusMinutes(service.getDurationMinutes());
            boolean       isPast      = cursor.isBefore(LocalDateTime.now());
            boolean       hasConflict = !appointmentRepository.findOverlapping(barber, cursor, slotEnd).isEmpty();
            boolean       isLunch     = isWeekday(date) && overlapsLunch(cursor.toLocalTime(), slotEnd.toLocalTime());

            if (!isPast && !hasConflict && !isLunch) {
                slots.add(cursor);
            }
            cursor = cursor.plusMinutes(30);
        }
        return slots;
    }

    public List<AppointmentResponse> myAppointments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return appointmentRepository.findByClientOrderByStartTimeDesc(user)
                .stream().map(AppointmentResponse::from).toList();
    }

    public List<AppointmentResponse> barberAppointments(UUID barberId, LocalDate date) {
        Barber barber = barberService.findById(barberId);
        if (date != null) {
            return appointmentRepository
                    .findByBarberAndStartTimeBetweenOrderByStartTimeAsc(
                            barber,
                            date.atStartOfDay(),
                            date.plusDays(1).atStartOfDay())
                    .stream().map(AppointmentResponse::from).toList();
        }
        return appointmentRepository.findByBarberOrderByStartTimeDesc(barber)
                .stream().map(AppointmentResponse::from).toList();
    }

    @Transactional
    public AppointmentResponse cancel(UUID id, String requesterEmail) {
        Appointment appointment = findEntityById(id);

        boolean isOwner = appointment.getClient().getEmail().equalsIgnoreCase(requesterEmail);
        boolean isAdmin = userRepository.findByEmail(requesterEmail)
                .map(u -> u.getRole() == Role.BARBER_ADMIN).orElse(false);

        if (!isOwner && !isAdmin) {
            throw new BadRequestException("No tienes permiso para cancelar esta cita");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse markCompleted(UUID id) {
        Appointment appointment = findEntityById(id);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    public AppointmentResponse getById(UUID id) {
        return AppointmentResponse.from(findEntityById(id));
    }

    private Appointment findEntityById(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cita no encontrada"));
    }

    private void validateWorkingHours(LocalDateTime start, LocalDateTime end, Barber barber) {
        if (start.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new BadRequestException("El local esta cerrado los domingos");
        }
        if (!start.toLocalDate().equals(end.toLocalDate())) {
            throw new BadRequestException("La cita debe terminar el mismo dia");
        }
        LocalTime workStart = parseTimeOrDefault(barber.getWorkStart(), businessStart(start.toLocalDate()));
        LocalTime workEnd   = parseTimeOrDefault(barber.getWorkEnd(), CLOSE);
        if (start.toLocalTime().isBefore(workStart) || end.toLocalTime().isAfter(workEnd)) {
            throw new BadRequestException(
                    "Horario fuera del rango de atencion del barbero (" + workStart + " - " + workEnd + ")");
        }
        if (isWeekday(start.toLocalDate()) && overlapsLunch(start.toLocalTime(), end.toLocalTime())) {
            throw new BadRequestException("Horario no disponible por almuerzo (16:00 - 17:00)");
        }
    }

    private void validateClientCanBook(User client) {
        if (!client.isEmailVerified()) {
            throw new BadRequestException("Verifica tu email antes de reservar.");
        }
        if (client.getPhone() == null || !CHILE_PHONE.matcher(client.getPhone().trim()).matches()) {
            throw new BadRequestException("Necesitas un telefono chileno valido para reservar.");
        }
        String email = client.getEmail() == null ? "" : client.getEmail();
        int at = email.lastIndexOf('@');
        if (at < 0 || BLOCKED_EMAIL_DOMAINS.contains(email.substring(at + 1))) {
            throw new BadRequestException("Usa un email real para reservar.");
        }
        long activeFuture = appointmentRepository.countByClientAndStatusNotAndStartTimeAfter(
                client, AppointmentStatus.CANCELLED, LocalDateTime.now());
        if (activeFuture >= MAX_ACTIVE_FUTURE_APPOINTMENTS) {
            throw new BadRequestException("Ya tienes el maximo de " + MAX_ACTIVE_FUTURE_APPOINTMENTS + " reservas futuras activas.");
        }
    }

    private LocalTime businessStart(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY ? SATURDAY_OPEN : WEEKDAY_OPEN;
    }

    private boolean isWeekday(LocalDate date) {
        return date.getDayOfWeek().getValue() >= DayOfWeek.MONDAY.getValue()
                && date.getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue();
    }

    private boolean overlapsLunch(LocalTime start, LocalTime end) {
        return start.isBefore(LUNCH_END) && end.isAfter(LUNCH_START);
    }

    private LocalTime parseTimeOrDefault(String value, LocalTime fallback) {
        if (value == null || value.isBlank()) return fallback;
        try {
            return LocalTime.parse(value.trim());
        } catch (Exception e) {
            return fallback;
        }
    }
}