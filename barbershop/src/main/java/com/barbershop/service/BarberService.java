package com.barbershop.service;

import com.barbershop.dto.request.BarberRequest;
import com.barbershop.dto.response.BarberResponse;
import com.barbershop.exception.NotFoundException;
import com.barbershop.model.Barber;
import com.barbershop.model.User;
import com.barbershop.repository.BarberRepository;
import com.barbershop.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BarberService {

    private final BarberRepository barberRepository;
    private final UserRepository userRepository;

    public BarberService(BarberRepository barberRepository,
                         UserRepository userRepository) {
        this.barberRepository = barberRepository;
        this.userRepository = userRepository;
    }

    public List<BarberResponse> listAll() {
        return barberRepository.findAll()
                .stream()
                .map(BarberResponse::from)
                .toList();
    }

    public BarberResponse getById(UUID id) {
        return BarberResponse.from(findById(id));
    }

    public Barber findById(UUID id) {
        return barberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Barbero no encontrado: " + id));
    }

    public BarberResponse create(BarberRequest req) {
        Barber barber = Barber.builder()
                .name(req.name())
                .specialty(req.specialty())
                .photoUrl(req.photoUrl())
                .bio(req.bio())
                .workStart(req.workStart())
                .workEnd(req.workEnd())
                .build();

        // Vincular usuario si viene el userId
        if (req.userId() != null) {
            User user = userRepository.findById(req.userId())
                    .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + req.userId()));
            barber.setUser(user);
        }

        return BarberResponse.from(barberRepository.save(barber));
    }

    public BarberResponse update(UUID id, BarberRequest req) {
        Barber barber = findById(id);
        barber.setName(req.name());
        barber.setSpecialty(req.specialty());
        barber.setPhotoUrl(req.photoUrl());
        barber.setBio(req.bio());
        barber.setWorkStart(req.workStart());
        barber.setWorkEnd(req.workEnd());

        // Actualizar vínculo de usuario si viene
        if (req.userId() != null) {
            User user = userRepository.findById(req.userId())
                    .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + req.userId()));
            barber.setUser(user);
        }

        return BarberResponse.from(barberRepository.save(barber));
    }

    public void delete(UUID id) {
        barberRepository.delete(findById(id));
    }
}
