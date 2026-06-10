package com.barbershop.config;

import com.barbershop.model.Barber;
import com.barbershop.model.Service;
import com.barbershop.model.User;
import com.barbershop.model.enums.Role;
import com.barbershop.repository.BarberRepository;
import com.barbershop.repository.ServiceRepository;
import com.barbershop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    // ── Admin 1: Felipe (dueño y barbero) ───────────────────────────────────
    @Value("${app.seed.admin.email:blade.barberia1@gmail.com}")
    private String adminEmail;

    @Value("${app.seed.admin.password:Admin123!}")
    private String adminPassword;

    @Value("${app.seed.admin.name:Felipe Eliecer}")
    private String adminName;

    // ── Admin 2: Daniel (soporte técnico) ───────────────────────────────────
    @Value("${app.seed.admin2.email:danielorlandodiaz14@gmail.com}")
    private String admin2Email;

    @Value("${app.seed.admin2.password:Admin123!}")
    private String admin2Password;

    @Value("${app.seed.admin2.name:Soporte Tecnico}")
    private String admin2Name;

    // ── Barbero vinculado a agenda ───────────────────────────────────────────
    @Value("${app.seed.barber.email:blade.barberia1@gmail.com}")
    private String barberEmail;

    @Value("${app.seed.barber.name:Felipe Eliecer}")
    private String barberName;

    @Value("${app.business.phone:+56 9 9809 8449}")
    private String businessPhone;

    @Bean
    CommandLineRunner seed(UserRepository userRepo,
                           BarberRepository barberRepo,
                           ServiceRepository serviceRepo,
                           PasswordEncoder encoder) {
        return args -> {

            // Admin 1 — Felipe (dueño y barbero)
            User felipeAdmin = upsertUser(
                    userRepo, encoder,
                    adminEmail,
                    adminPassword,
                    adminName,
                    businessPhone,
                    Role.BARBER_ADMIN
            );

            // Admin 2 — Daniel (solo si el email es distinto al de Felipe)
            // Evita el bug de intentar crear dos usuarios con el mismo email
            if (!admin2Email.equalsIgnoreCase(adminEmail)) {
                upsertUser(
                        userRepo, encoder,
                        admin2Email,
                        admin2Password,
                        admin2Name,
                        businessPhone,
                        Role.BARBER_ADMIN
                );
            }

            // Barbero Felipe — vinculado al usuario cuyo email es barberEmail
            // En producción: barberEmail = blade.barberia1@gmail.com = felipeAdmin
            // En pruebas locales: puede apuntar a danielorlandodiaz14@gmail.com
            User barberUser = userRepo.findByEmail(barberEmail.toLowerCase().trim())
                    .orElse(felipeAdmin);
            upsertBarber(barberRepo, barberUser);

            // Servicios iniciales
            upsertService(serviceRepo, "Corte", "Corte normal",
                    "Corte tradicional, degradado bajo, medio, alto, mohicano o estilo general.",
                    30, "12000.00");
            upsertService(serviceRepo, "Corte + barba", null,
                    "Servicio completo de corte y perfilado de barba.",
                    60, "15000.00");
            upsertService(serviceRepo, "Barba", null,
                    "Perfilado, rebaje y terminaciones de barba.",
                    20, "5000.00");
        };
    }

    private User upsertUser(UserRepository userRepo,
                            PasswordEncoder encoder,
                            String email,
                            String password,
                            String name,
                            String phone,
                            Role role) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = userRepo.findByEmail(normalizedEmail).orElseGet(User::new);
        user.setEmail(normalizedEmail);
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(encoder.encode(password));
        }
        user.setFullName(name.trim());
        user.setPhone(phone.trim());
        user.setRole(role);
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);
        return userRepo.save(user);
    }

    private void upsertBarber(BarberRepository barberRepo, User barberUser) {
        Barber barber = barberRepo.findByNameIgnoreCase(barberName).orElseGet(Barber::new);
        barber.setName(barberName);
        barber.setSpecialty("Degradados, cortes tradicionales, mohicano y barba");
        barber.setBio("Barbero de ElPipeBarber en Maipu. Atencion en La Galaxia 2292.");
        barber.setWorkStart("10:45");
        barber.setWorkEnd("21:00");
        barber.setUser(barberUser);
        barberRepo.save(barber);
    }

    private void upsertService(ServiceRepository serviceRepo,
                               String name,
                               String legacyName,
                               String description,
                               int durationMinutes,
                               String price) {
        Service service = serviceRepo.findByNameIgnoreCase(name)
                .or(() -> legacyName == null
                        ? java.util.Optional.empty()
                        : serviceRepo.findByNameIgnoreCase(legacyName))
                .orElseGet(Service::new);

        service.setName(name);
        service.setDescription(description);
        service.setDurationMinutes(durationMinutes);
        service.setPrice(new BigDecimal(price));
        service.setActive(true);
        serviceRepo.save(service);
    }
}
