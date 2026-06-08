package com.barbearia.backend.repository;

import com.barbearia.backend.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    boolean existsByBarbeiroIdAndDataHora(Long barbeiroId, LocalDateTime dataHora);

    List<Agendamento> findByClienteUsuarioEmail(String email);
}
