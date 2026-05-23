package com.barbershop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Estado del servidor")
public class HealthController {

    @Operation(summary = "Health check")
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "ok",
                "service", "barbershop-backend"
        );
    }
}