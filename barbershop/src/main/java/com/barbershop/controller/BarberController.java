package com.barbershop.controller;

import com.barbershop.dto.request.BarberRequest;
import com.barbershop.dto.response.BarberResponse;
import com.barbershop.service.BarberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/barbers")
@Tag(name = "Barbers", description = "Gestión de barberos")
public class BarberController {

    private final BarberService barberService;

    public BarberController(BarberService barberService) {
        this.barberService = barberService;
    }

    // público: cualquiera puede ver los barberos
    @Operation(summary = "Listar todos los barberos")
    @GetMapping
    public List<BarberResponse> list() {
        return barberService.listAll();
    }

    @Operation(summary = "Obtener barbero por ID")
    @GetMapping("/{id}")
    public BarberResponse getById(@PathVariable UUID id) {
        return barberService.getById(id);
    }

    // solo BARBER_ADMIN (controlado en SecurityConfig)
    @Operation(summary = "Crear barbero — solo Admin")
    @PostMapping
    public ResponseEntity<BarberResponse> create(
            @Valid @RequestBody BarberRequest req) {
        return ResponseEntity.ok(barberService.create(req));
    }

    @Operation(summary = "Actualizar barbero — solo Admin")
    @PutMapping("/{id}")
    public BarberResponse update(@PathVariable UUID id,
                                 @Valid @RequestBody BarberRequest req) {
        return barberService.update(id, req);
    }

    @Operation(summary = "Eliminar barbero — solo Admin")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        barberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}