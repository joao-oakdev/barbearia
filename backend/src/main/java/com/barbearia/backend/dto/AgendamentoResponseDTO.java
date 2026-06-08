package com.barbearia.backend.dto;

import com.barbearia.backend.enums.StatusAgendamentoEnum;

import java.time.LocalDateTime;


public record AgendamentoResponseDTO(
        Long id,
        String nomeCliente,
        String nomeBarbeiro,
        LocalDateTime dataHora,
        StatusAgendamentoEnum status
) {
}
