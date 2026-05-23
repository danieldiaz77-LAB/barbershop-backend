package com.barbershop.repository;

import com.barbershop.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<Service, UUID> {

    // retorna solo los servicios activos para el catálogo público
    List<Service> findByActiveTrue();
}