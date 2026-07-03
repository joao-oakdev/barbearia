package com.barbearia.backend.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FILA_AGENDAMENTO = "fila.agendamento.criado";

    @Bean
    public Queue filaAgendamento() {
        return new Queue(FILA_AGENDAMENTO, true);
    }
}