package com.barbershop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// se lanza cuando hay conflicto de horario al agendar
@ResponseStatus(HttpStatus.CONFLICT)
public class SlotConflictException extends RuntimeException {

    public SlotConflictException(String message) {
        super(message);
    }
}