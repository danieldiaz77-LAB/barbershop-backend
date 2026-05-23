package com.barbershop.repository;

import com.barbershop.model.Appointment;
import com.barbershop.model.Barber;
import com.barbershop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // detecta citas que se solapan con el rango [start, end)
    // regla de solapamiento: a.startTime < end Y a.endTime > start
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.barber = :barber
          AND a.status <> com.barbershop.model.enums.AppointmentStatus.CANCELLED
          AND a.startTime < :end
          AND a.endTime   > :start
    """)
    List<Appointment> findOverlapping(@Param("barber") Barber barber,
                                      @Param("start") LocalDateTime start,
                                      @Param("end")   LocalDateTime end);

    // historial del cliente ordenado por fecha descendente
    List<Appointment> findByClientOrderByStartTimeDesc(User client);

    // citas de un barbero en un rango de fechas (para ver agenda del día)
    List<Appointment> findByBarberAndStartTimeBetweenOrderByStartTimeAsc(
            Barber barber, LocalDateTime from, LocalDateTime to);

    // todas las citas de un barbero
    List<Appointment> findByBarberOrderByStartTimeDesc(Barber barber);
}