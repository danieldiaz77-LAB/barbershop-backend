package com.barbershop.repository;

import com.barbershop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // busca usuario por email para login
    Optional<User> findByEmail(String email);

    // verifica si el email ya está registrado
    boolean existsByEmail(String email);
}