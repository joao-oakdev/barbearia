package com.barbearia.backend.controller;

import com.barbearia.backend.dto.AgendamentoRequestDTO;
import com.barbearia.backend.dto.AgendamentoResponseDTO;
import com.barbearia.backend.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;


    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> criarAgendamento(@RequestBody @Valid AgendamentoRequestDTO dto, @AuthenticationPrincipal UserDetails userDetails) {
        AgendamentoResponseDTO resultado = agendamentoService.criarAgendamento(dto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<LocalDateTime>> listarHorariosDisponiveis(@RequestParam Long barbeiroId, @RequestParam LocalDate data) {
        List<LocalDateTime> lista = agendamentoService.listarHorariosDisponiveis(barbeiroId, data);
        return ResponseEntity.ok(lista);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarAgendamento(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        agendamentoService.cancelarAgendamento(id, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/meus-agendamentos")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarMeusAgendamentos(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(agendamentoService.listarMeusAgendamentos(userDetails.getUsername()));
    }
}
