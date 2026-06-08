package com.barbearia.backend.model;

import com.barbearia.backend.enums.StatusAgendamentoEnum;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "barbeiro_id")
    private Barbeiro barbeiro;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne
    @JoinColumn(nullable = false, name = "cliente_id")
    private Cliente cliente;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusAgendamentoEnum status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Barbeiro getBarbeiro() {
        return barbeiro;
    }

    public void setBarbeiro(Barbeiro barbeiro) {
        this.barbeiro = barbeiro;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public StatusAgendamentoEnum getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamentoEnum status) {
        this.status = status;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
