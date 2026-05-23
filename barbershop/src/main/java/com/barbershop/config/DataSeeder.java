package com.barbershop.config;

import com.barbershop.model.Barber;
import com.barbershop.model.Service;
import com.barbershop.model.User;
import com.barbershop.model.enums.Role;
import com.barbershop.repository.BarberRepository;
import com.barbershop.repository.ServiceRepository;
import com.barbershop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

// carga datos iniciales al arrancar la aplicación
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(UserRepository userRepo,
                           BarberRepository barberRepo,
                           ServiceRepository serviceRepo,
                           PasswordEncoder encoder) {
        return args -> {

            // usuario admin
            if (!userRepo.existsByEmail("admin@barbershop.com")) {
                userRepo.save(User.builder()
                        .email("admin@barbershop.com")
                        .password(encoder.encode("Admin123!"))
                        .fullName("Marco Aurelio")
                        .role(Role.BARBER_ADMIN)
                        .phone("+1 555 010 0001")
                        .build());
            }

            // usuario cliente de prueba
            if (!userRepo.existsByEmail("client@barbershop.com")) {
                userRepo.save(User.builder()
                        .email("client@barbershop.com")
                        .password(encoder.encode("Client123!"))
                        .fullName("Lucas Demo")
                        .role(Role.CLIENT)
                        .phone("+1 555 010 0002")
                        .build());
            }

            // barberos iniciales
            if (barberRepo.count() == 0) {
                barberRepo.save(Barber.builder()
                        .name("Marco Aurelio")
                        .specialty("Classic Cuts & Hot Towel Shaves")
                        .bio("Master barber con 12+ años de experiencia.")
                        .workStart("09:00")
                        .workEnd("19:00")
                        .build());

                barberRepo.save(Barber.builder()
                        .name("Rafael Cruz")
                        .specialty("Fades & Modern Styling")
                        .bio("Precisión en fades y diseños contemporáneos.")
                        .workStart("10:00")
                        .workEnd("20:00")
                        .build());

                barberRepo.save(Barber.builder()
                        .name("Diego Silva")
                        .specialty("Beard Sculpting")
                        .bio("Artista de barba especializado en grooming.")
                        .workStart("09:00")
                        .workEnd("18:00")
                        .build());
            }

            // servicios iniciales
            if (serviceRepo.count() == 0) {
                serviceRepo.save(Service.builder()
                        .name("Classic Haircut")
                        .description("Corte tijera clásico, lavado y peinado.")
                        .durationMinutes(45)
                        .price(new BigDecimal("28.00"))
                        .active(true).build());

                serviceRepo.save(Service.builder()
                        .name("Beard Trim & Shape")
                        .description("Escultura de barba con toalla caliente.")
                        .durationMinutes(30)
                        .price(new BigDecimal("18.00"))
                        .active(true).build());

                serviceRepo.save(Service.builder()
                        .name("Hot Towel Shave")
                        .description("Afeitado clásico con navaja recta.")
                        .durationMinutes(45)
                        .price(new BigDecimal("32.00"))
                        .active(true).build());

                serviceRepo.save(Service.builder()
                        .name("Cut + Beard Combo")
                        .description("Combo completo: corte + barba.")
                        .durationMinutes(75)
                        .price(new BigDecimal("42.00"))
                        .active(true).build());

                serviceRepo.save(Service.builder()
                        .name("Kids Cut")
                        .description("Corte amigable para niños menores de 12.")
                        .durationMinutes(30)
                        .price(new BigDecimal("20.00"))
                        .active(true).build());
            }
        };
    }
}