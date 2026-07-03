package com.barbearia.backend.messaging;

import com.barbearia.backend.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AgendamentoProducer {

    private static final Logger logger = LoggerFactory.getLogger(AgendamentoProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public AgendamentoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarAgendamentoCriado(Long agendamentoId, String emailCliente) {
        String mensagem = "Agendamento criado — id: " + agendamentoId + ", cliente: " + emailCliente;
        rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_AGENDAMENTO, mensagem);
        logger.info("Mensagem publicada na fila: {}", mensagem);
    }
}