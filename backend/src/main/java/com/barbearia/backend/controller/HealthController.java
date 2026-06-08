package com.barbearia.backend.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "Barbearia API rodando!";
    }

    @GetMapping("/health/gerar-senha")
    public String gerarSenha(@RequestParam String senha) {
        return new BCryptPasswordEncoder().encode(senha);
    }
}