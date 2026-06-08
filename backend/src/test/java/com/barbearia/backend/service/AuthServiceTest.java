package com.barbearia.backend.service;

import com.barbearia.backend.dto.LoginRequestDTO;
import com.barbearia.backend.dto.RegistroRequestDTO;
import com.barbearia.backend.enums.TipoUsuarioEnum;
import com.barbearia.backend.exception.EmailJaCadastradoException;
import com.barbearia.backend.exception.NotFoundException;
import com.barbearia.backend.exception.SenhaInvalidaException;
import com.barbearia.backend.model.Cliente;
import com.barbearia.backend.model.Usuario;
import com.barbearia.backend.repository.ClienteRepository;
import com.barbearia.backend.repository.UsuarioRepository;
import com.barbearia.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveRealizarLoginComSucesso() {
        // Arrange — prepara os dados
        LoginRequestDTO dto = new LoginRequestDTO("joao@email.com", "123456");

        Usuario usuario = new Usuario();
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senhaCriptografada");
        usuario.setTipoUsuario(TipoUsuarioEnum.CLIENTE);

        // Ensina o mock o que retornar
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "senhaCriptografada")).thenReturn(true);
        when(jwtService.gerarToken("joao@email.com")).thenReturn("token-fake");

        // Act — executa o login
        var resultado = authService.login(dto);

        // Assert — verifica o resultado
        assertNotNull(resultado);
        assertEquals("token-fake", resultado.token());
        assertEquals(TipoUsuarioEnum.CLIENTE, resultado.tipoUsuario());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO("naoexiste@email.com", "123456");

        when(usuarioRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> authService.login(dto));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaInvalida() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO("joao@email.com", "senhaErrada");

        Usuario usuario = new Usuario();
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senhaCriptografada");

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", "senhaCriptografada")).thenReturn(false);

        // Act & Assert
        assertThrows(SenhaInvalidaException.class, () -> authService.login(dto));
    }

    @Test
    void deveRegistrarClienteComSucesso() {
        // Arrange
        RegistroRequestDTO dto = new RegistroRequestDTO("João", "joao@email.com", "123456", "41999999999");

        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("senhaCriptografada");

        // Act
        authService.registro(dto);

        // Assert — verifica que o save foi chamado uma vez
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // Arrange
        RegistroRequestDTO dto = new RegistroRequestDTO("João", "joao@email.com", "123456", "41999999999");

        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(true);

        // Act & Assert
        assertThrows(EmailJaCadastradoException.class, () -> authService.registro(dto));
    }

}