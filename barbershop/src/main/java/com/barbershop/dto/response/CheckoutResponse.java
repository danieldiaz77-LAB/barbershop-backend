package com.barbershop.dto.response;

public record CheckoutResponse(
        String sessionId,
        String url
) {}