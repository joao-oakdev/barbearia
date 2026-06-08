package com.barbearia.backend.service;

import com.barbearia.backend.dto.AgendamentoRequestDTO;
import com.barbearia.backend.enums.StatusAgendamentoEnum;
import com.barbearia.backend.exception.HorarioIndisponivelException;
import com.barbearia.backend.exception.NotFoundException;
import com.barbearia.backend.exception.SemPermissaoException;
import com.barbearia.backend.model.Agendamento;
import com.barbearia.backend.model.Barbeiro;
import com.barbearia.backend.model.Cliente;
import com.barbearia.backend.model.Usuario;
import com.barbearia.backend.repository.AgendamentoRepository;
import com.barbearia.backend.repository.BarbeiroRepository;
import com.barbearia.backend.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.eq;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private BarbeiroRepository barbeiroRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    @Test
    void deveCriarAgendamentoComSucesso() {
        // Arrange
        AgendamentoRequestDTO dto = new AgendamentoRequestDTO(1L, LocalDateTime.of(2026, 6, 10, 9, 0));

        Usuario usuarioBarbeiro = new Usuario();
        usuarioBarbeiro.setNome("Carlos Barbeiro");

        Barbeiro barbeiro = new Barbeiro();
        barbeiro.setUsuario(usuarioBarbeiro);

        Usuario usuarioCliente = new Usuario();
        usuarioCliente.setNome("João Cliente");
        usuarioCliente.setEmail("joao@email.com");

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuarioCliente);

        Agendamento agendamentoSalvo = new Agendamento();
        agendamentoSalvo.setId(1L);
        agendamentoSalvo.setBarbeiro(barbeiro);
        agendamentoSalvo.setCliente(cliente);
        agendamentoSalvo.setDataHora(dto.dataHora());
        agendamentoSalvo.setStatus(StatusAgendamentoEnum.AGENDADO);

        when(barbeiroRepository.findById(1L)).thenReturn(Optional.of(barbeiro));
        when(clienteRepository.findByUsuarioEmail("joao@email.com")).thenReturn(Optional.of(cliente));
        when(agendamentoRepository.existsByBarbeiroIdAndDataHora(1L, dto.dataHora())).thenReturn(false);
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoSalvo);

        // Act
        var resultado = agendamentoService.criarAgendamento(dto, "joao@email.com");

        // Assert
        assertNotNull(resultado);
        assertEquals("Carlos Barbeiro", resultado.nomeBarbeiro());
        assertEquals("João Cliente", resultado.nomeCliente());
        assertEquals(StatusAgendamentoEnum.AGENDADO, resultado.status());
    }

    @Test
    void deveLancarExcecaoQuandoBarbeiroNaoEncontrado() {
        // Arrange
        AgendamentoRequestDTO dto = new AgendamentoRequestDTO(99L, LocalDateTime.of(2026, 6, 10, 9, 0));

        when(barbeiroRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> agendamentoService.criarAgendamento(dto, "joao@email.com"));
    }

    @Test
    void deveLancarExcecaoQuandoHorarioIndisponivel() {
        // Arrange
        AgendamentoRequestDTO dto = new AgendamentoRequestDTO(1L, LocalDateTime.of(2026, 6, 10, 9, 0));

        Barbeiro barbeiro = new Barbeiro();
        barbeiro.setUsuario(new Usuario());

        Cliente cliente = new Cliente();
        cliente.setUsuario(new Usuario());

        when(barbeiroRepository.findById(1L)).thenReturn(Optional.of(barbeiro));
        when(clienteRepository.findByUsuarioEmail("joao@email.com")).thenReturn(Optional.of(cliente));
        when(agendamentoRepository.existsByBarbeiroIdAndDataHora(1L, dto.dataHora())).thenReturn(true);

        // Act & Assert
        assertThrows(HorarioIndisponivelException.class, () -> agendamentoService.criarAgendamento(dto, "joao@email.com"));
    }

    @Test
    void deveCancelarAgendamentoComSucesso() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setEmail("joao@email.com");

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);

        Agendamento agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setCliente(cliente);
        agendamento.setStatus(StatusAgendamentoEnum.AGENDADO);

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        // Act
        agendamentoService.cancelarAgendamento(1L, "joao@email.com");

        // Assert
        verify(agendamentoRepository, times(1)).save(agendamento);
        assertEquals(StatusAgendamentoEnum.CANCELADO, agendamento.getStatus());
    }

    @Test
    void deveLancarExcecaoQuandoAgendamentoNaoEncontrado() {
        // Arrange
        when(agendamentoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> agendamentoService.cancelarAgendamento(99L, "joao@email.com"));
    }

    @Test
    void deveLancarExcecaoQuandoClienteSemPermissao() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setEmail("outro@email.com");

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        // Act & Assert
        assertThrows(SemPermissaoException.class, () -> agendamentoService.cancelarAgendamento(1L, "joao@email.com"));
    }

    @Test
    void deveListarHorariosDisponiveis() {
        // Arrange
        Long barbeiroId = 1L;
        LocalDate data = LocalDate.of(2026, 6, 10);

        // Simula que o horário das 9h está ocupado e os outros estão livres
        when(agendamentoRepository.existsByBarbeiroIdAndDataHora(eq(barbeiroId), eq(data.atTime(9, 0)))).thenReturn(true);
        when(agendamentoRepository.existsByBarbeiroIdAndDataHora(eq(barbeiroId), eq(data.atTime(10, 0)))).thenReturn(false);
        when(agendamentoRepository.existsByBarbeiroIdAndDataHora(eq(barbeiroId), eq(data.atTime(11, 0)))).thenReturn(false);
        when(agendamentoRepository.existsByBarbeiroIdAndDataHora(eq(barbeiroId), eq(data.atTime(14, 0)))).thenReturn(false);
        when(agendamentoRepository.existsByBarbeiroIdAndDataHora(eq(barbeiroId), eq(data.atTime(15, 0)))).thenReturn(false);
        when(agendamentoRepository.existsByBarbeiroIdAndDataHora(eq(barbeiroId), eq(data.atTime(16, 0)))).thenReturn(false);

        // Act
        List<LocalDateTime> horarios = agendamentoService.listarHorariosDisponiveis(barbeiroId, data);

        // Assert
        assertEquals(5, horarios.size()); // 9h estava ocupado, sobraram 5
        assertFalse(horarios.contains(data.atTime(9, 0))); // 9h não deve estar na lista
        assertTrue(horarios.contains(data.atTime(10, 0))); // 10h deve estar
    }

}