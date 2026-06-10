package com.barbershop.config;

import com.barbershop.model.Appointment;
import com.barbershop.model.Barber;
import com.barbershop.model.enums.AppointmentStatus;
import com.barbershop.repository.AppointmentRepository;
import com.barbershop.repository.BarberRepository;
import com.barbershop.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RestController
@RequestMapping("/api/admin")
public class AgendaScheduler {

    private static final ZoneId CHILE_ZONE = ZoneId.of("America/Santiago");

    private final BarberRepository barberRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    public AgendaScheduler(BarberRepository barberRepository,
                           AppointmentRepository appointmentRepository,
                           EmailService emailService) {
        this.barberRepository = barberRepository;
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
    }

    // ── Disparo automático cada día a las 8:00 AM lun-sáb ───────────────────
    @Scheduled(cron = "0 0 8 * * MON-SAT", zone = "America/Santiago")
    public void enviarAgendasDiarias() {
        dispararAgendasHoy();
    }

    // ── Disparo manual desde el panel admin (solo BARBER_ADMIN) ─────────────
    // POST /api/admin/agenda-test → envía el email de agenda a todos los barberos
    @PostMapping("/agenda-test")
    @PreAuthorize("hasRole('BARBER_ADMIN')")
    public ResponseEntity<String> triggerManual() {
        int enviados = dispararAgendasHoy();
        return ResponseEntity.ok("Agenda enviada a " + enviados + " barbero(s).");
    }

    // ── Lógica compartida ────────────────────────────────────────────────────
    private int dispararAgendasHoy() {
        LocalDate hoy = LocalDate.now(CHILE_ZONE);
        LocalDateTime desde = hoy.atStartOfDay();
        LocalDateTime hasta = hoy.plusDays(1).atStartOfDay();

        List<Barber> barberos = barberRepository.findAll();
        int count = 0;

        for (Barber barber : barberos) {
            if (barber.getUser() == null) continue;

            List<Appointment> citasHoy = appointmentRepository
                    .findByBarberAndStartTimeBetweenOrderByStartTimeAsc(barber, desde, hasta)
                    .stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.PENDING)
                    .toList();

            emailService.enviarAgendaDiaria(barber, citasHoy);
            count++;
        }

        return count;
    }
}
