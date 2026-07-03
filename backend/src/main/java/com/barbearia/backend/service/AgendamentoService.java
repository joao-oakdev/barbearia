package com.barbearia.backend.service;

import com.barbearia.backend.dto.AgendamentoRequestDTO;
import com.barbearia.backend.dto.AgendamentoResponseDTO;
import com.barbearia.backend.enums.StatusAgendamentoEnum;
import com.barbearia.backend.exception.HorarioIndisponivelException;
import com.barbearia.backend.exception.NotFoundException;
import com.barbearia.backend.exception.SemPermissaoException;
import com.barbearia.backend.messaging.AgendamentoProducer;
import com.barbearia.backend.model.Agendamento;
import com.barbearia.backend.model.Barbeiro;
import com.barbearia.backend.model.Cliente;
import com.barbearia.backend.repository.AgendamentoRepository;
import com.barbearia.backend.repository.BarbeiroRepository;
import com.barbearia.backend.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {

    private static final Logger logger = LoggerFactory.getLogger(AgendamentoService.class);
    private final AgendamentoProducer agendamentoProducer;
    private final AgendamentoRepository agendamentoRepository;
    private final BarbeiroRepository barbeiroRepository;
    private final ClienteRepository clienteRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository, BarbeiroRepository barbeiroRepository, ClienteRepository clienteRepository, AgendamentoProducer agendamentoProducer) {
        this.agendamentoRepository = agendamentoRepository;
        this.barbeiroRepository = barbeiroRepository;
        this.clienteRepository = clienteRepository;
        this.agendamentoProducer = agendamentoProducer;

    }

    public AgendamentoResponseDTO criarAgendamento(AgendamentoRequestDTO dto, String emailCliente) {
        logger.info("Tentativa de agendamento — cliente: {}, barbeiroId: {}", emailCliente, dto.barbeiroId());

        Barbeiro barbeiro = barbeiroRepository.findById(dto.barbeiroId()).orElseThrow(() -> {
            logger.warn("Agendamento falhou — barbeiro não encontrado: {}", dto.barbeiroId());
            return new NotFoundException("Barbeiro não encontrado");
        });

        Cliente cliente = clienteRepository.findByUsuarioEmail(emailCliente).orElseThrow(() -> {
            logger.warn("Agendamento falhou — cliente não encontrado: {}", emailCliente);
            return new NotFoundException("Cliente não encontrado");
        });

        if (agendamentoRepository.existsByBarbeiroIdAndDataHora(dto.barbeiroId(), dto.dataHora())) {
            logger.warn("Agendamento falhou — horário indisponível: {}", dto.dataHora());
            throw new HorarioIndisponivelException("Horário indisponível");
        }

        Agendamento novoAgendamento = new Agendamento();
        novoAgendamento.setBarbeiro(barbeiro);
        novoAgendamento.setCliente(cliente);
        novoAgendamento.setDataHora(dto.dataHora());
        novoAgendamento.setStatus(StatusAgendamentoEnum.AGENDADO);

        Agendamento salvo = agendamentoRepository.save(novoAgendamento);
        logger.info("Agendamento criado com sucesso — id: {}, cliente: {}", salvo.getId(), emailCliente);

        AgendamentoResponseDTO responseDTO = new AgendamentoResponseDTO(
                salvo.getId(),
                salvo.getCliente().getUsuario().getNome(),
                salvo.getBarbeiro().getUsuario().getNome(),
                salvo.getDataHora(),
                salvo.getStatus()
        );

        agendamentoProducer.publicarAgendamentoCriado(salvo.getId(), emailCliente);

        return responseDTO;
    }

    public List<LocalDateTime> listarHorariosDisponiveis(Long barbeiroId, LocalDate data) {
        logger.info("Listando horários disponíveis — barbeiroId: {}, data: {}", barbeiroId, data);

        List<LocalDateTime> todosHorarios = List.of(
                data.atTime(9, 0),
                data.atTime(10, 0),
                data.atTime(11, 0),
                data.atTime(14, 0),
                data.atTime(15, 0),
                data.atTime(16, 0)
        );

        return todosHorarios.stream()
                .filter(horario -> !agendamentoRepository.existsByBarbeiroIdAndDataHora(barbeiroId, horario))
                .toList();
    }

    public void cancelarAgendamento(Long id, String emailCliente) {
        logger.info("Tentativa de cancelamento — agendamentoId: {}, cliente: {}", id, emailCliente);

        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cancelamento falhou — agendamento não encontrado: {}", id);
                    return new NotFoundException("Agendamento não encontrado");
                });

        if (!agendamento.getCliente().getUsuario().getEmail().equals(emailCliente)) {
            logger.warn("Cancelamento falhou — sem permissão. Cliente: {}, dono: {}", emailCliente, agendamento.getCliente().getUsuario().getEmail());
            throw new SemPermissaoException("Sem permissão");
        }

        agendamento.setStatus(StatusAgendamentoEnum.CANCELADO);
        agendamentoRepository.save(agendamento);
        logger.info("Agendamento cancelado com sucesso — id: {}", id);
    }

    public List<AgendamentoResponseDTO> listarMeusAgendamentos(String emailCliente) {
        logger.info("Listando agendamentos do cliente: {}", emailCliente);

        return agendamentoRepository.findByClienteUsuarioEmail(emailCliente)
                .stream()
                .map(agendamento -> new AgendamentoResponseDTO(
                        agendamento.getId(),
                        agendamento.getCliente().getUsuario().getNome(),
                        agendamento.getBarbeiro().getUsuario().getNome(),
                        agendamento.getDataHora(),
                        agendamento.getStatus()
                ))
                .toList();
    }

}
