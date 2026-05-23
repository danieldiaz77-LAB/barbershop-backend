package com.barbershop.controller;

import com.barbershop.dto.request.ServiceRequest;
import com.barbershop.dto.response.ServiceResponse;
import com.barbershop.service.ServiceCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/services")
@Tag(name = "Services", description = "Catálogo de servicios")
public class ServiceController {

    private final ServiceCatalogService catalogService;

    public ServiceController(ServiceCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // público: solo servicios activos
    @Operation(summary = "Listar servicios activos")
    @GetMapping
    public List<ServiceResponse> list() {
        return catalogService.listActive();
    }

    // admin: todos los servicios incluyendo inactivos
    @Operation(summary = "Listar todos los servicios — solo Admin")
    @GetMapping("/all")
    public List<ServiceResponse> listAll() {
        return catalogService.listAll();
    }

    @Operation(summary = "Crear servicio — solo Admin")
    @PostMapping
    public ResponseEntity<ServiceResponse> create(
            @Valid @RequestBody ServiceRequest req) {
        return ResponseEntity.ok(catalogService.create(req));
    }

    @Operation(summary = "Actualizar servicio — solo Admin")
    @PutMapping("/{id}")
    public ServiceResponse update(@PathVariable UUID id,
                                  @Valid @RequestBody ServiceRequest req) {
        return catalogService.update(id, req);
    }

    // borrado lógico: marca inactivo
    @Operation(summary = "Desactivar servicio — solo Admin")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        catalogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}