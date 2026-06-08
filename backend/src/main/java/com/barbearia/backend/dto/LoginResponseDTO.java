package com.barbearia.backend.dto;

import com.barbearia.backend.enums.TipoUsuarioEnum;

public record LoginResponseDTO(

        String token,

        TipoUsuarioEnum tipoUsuario
) {
}
