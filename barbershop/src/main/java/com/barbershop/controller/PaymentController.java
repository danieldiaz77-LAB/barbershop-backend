package com.barbershop.controller;

import com.barbershop.dto.request.CheckoutRequest;
import com.barbershop.dto.response.CheckoutResponse;
import com.barbershop.dto.response.PaymentResponse;
import com.barbershop.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Pagos con Stripe")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // crea sesión de checkout en Stripe
    @Operation(summary = "Crear sesión de pago Stripe")
    @PostMapping("/checkout")
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest req,
                                     Authentication auth) {
        return paymentService.createCheckout(req, auth.getName());
    }

    // verifica si el pago fue exitoso (se llama al volver de Stripe)
    @Operation(summary = "Verificar estado del pago por session ID")
    @GetMapping("/status/{sessionId}")
    public PaymentResponse status(@PathVariable String sessionId) {
        return paymentService.checkStatus(sessionId);
    }
}