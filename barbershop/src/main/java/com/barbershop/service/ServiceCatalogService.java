package com.barbershop.service;

import com.barbershop.dto.request.ServiceRequest;
import com.barbershop.dto.response.ServiceResponse;
import com.barbershop.exception.NotFoundException;
import com.barbershop.model.Service;
import com.barbershop.repository.ServiceRepository;

import java.util.List;
import java.util.UUID;

// @Service explícito porque la clase se llama igual que java.util.Service
@org.springframework.stereotype.Service
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;

    public ServiceCatalogService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    // solo retorna servicios activos para el catálogo público
    public List<ServiceResponse> listActive() {
        return serviceRepository.findByActiveTrue()
                .stream()
                .map(ServiceResponse::from)
                .toList();
    }

    // retorna todos los servicios para el panel admin
    public List<ServiceResponse> listAll() {
        return serviceRepository.findAll()
                .stream()
                .map(ServiceResponse::from)
                .toList();
    }

    // método interno reutilizable que retorna la entidad
    public Service findById(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Servicio no encontrado: " + id));
    }

    public ServiceResponse create(ServiceRequest req) {
        Service service = Service.builder()
                .name(req.name())
                .description(req.description())
                .durationMinutes(req.durationMinutes())
                .price(req.price())
                .active(req.active() == null ? Boolean.TRUE : req.active())
                .build();
        return ServiceResponse.from(serviceRepository.save(service));
    }

    public ServiceResponse update(UUID id, ServiceRequest req) {
        Service service = findById(id);
        service.setName(req.name());
        service.setDescription(req.description());
        service.setDurationMinutes(req.durationMinutes());
        service.setPrice(req.price());
        if (req.active() != null) service.setActive(req.active());
        return ServiceResponse.from(serviceRepository.save(service));
    }

    // borrado lógico: marca como inactivo en lugar de eliminar
    public void delete(UUID id) {
        Service service = findById(id);
        service.setActive(false);
        serviceRepository.save(service);
    }
}