package com.barbearia.backend.repository;

import com.barbearia.backend.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsuarioEmail(String email);
}
