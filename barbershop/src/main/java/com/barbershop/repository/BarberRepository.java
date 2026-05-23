package com.barbershop.repository;

import com.barbershop.model.Barber;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BarberRepository extends JpaRepository<Barber, UUID> {
    // JpaRepository ya trae findAll, findById, save, delete
}