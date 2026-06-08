package com.barbearia.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AgendamentoRequestDTO(
        @NotNull
        Long barbeiroId,
        @NotNull
        LocalDateTime dataHora
) {
}
