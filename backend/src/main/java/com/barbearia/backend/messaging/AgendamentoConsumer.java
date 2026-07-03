package com.barbearia.backend.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.barbearia.backend.config.RabbitMQConfig;

@Component
public class AgendamentoConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AgendamentoConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.FILA_AGENDAMENTO)
    public void processarAgendamento(String mensagem) {
        logger.info("Mensagem recebida da fila: {}", mensagem);
        logger.info("Simulando envio de notificação: {}", mensagem);
    }
}