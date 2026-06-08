package com.barbearia.backend.repository;

import com.barbearia.backend.model.Barbeiro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarbeiroRepository extends JpaRepository<Barbeiro, Long> {
}
