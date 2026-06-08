package com.barbearia.backend.service;

import com.barbearia.backend.dto.LoginRequestDTO;
import com.barbearia.backend.dto.LoginResponseDTO;
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
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ClienteRepository clienteRepository;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService, ClienteRepository clienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.clienteRepository = clienteRepository;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        logger.info("Tentativa de login para o email: {}", dto.email());

        Usuario usuario = usuarioRepository.findByEmail(dto.email()).orElseThrow(() -> {
            logger.warn("Login falhou — usuário não encontrado: {}", dto.email());
            return new NotFoundException("Usuário não encontrado");
        });

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            logger.warn("Login falhou — senha inválida para: {}", dto.email());
            throw new SenhaInvalidaException("Senha inválida");
        }

        String token = jwtService.gerarToken(usuario.getEmail());
        logger.info("Login realizado com sucesso para: {}", dto.email());

        return new LoginResponseDTO(token, usuario.getTipoUsuario());
    }

    @Transactional
    public void registro(RegistroRequestDTO dto) {
        logger.info("Tentativa de registro para o email: {}", dto.email());

        if (usuarioRepository.existsByEmail(dto.email())) {
            logger.warn("Registro falhou — email já cadastrado: {}", dto.email());
            throw new EmailJaCadastradoException("Email já cadastrado");
        }

        Usuario usuarioNovo = new Usuario();
        usuarioNovo.setNome(dto.nome());
        usuarioNovo.setEmail(dto.email());
        usuarioNovo.setSenha(passwordEncoder.encode(dto.senha()));
        usuarioNovo.setTipoUsuario(TipoUsuarioEnum.CLIENTE);
        usuarioRepository.save(usuarioNovo);

        Cliente clienteNovo = new Cliente();
        clienteNovo.setUsuario(usuarioNovo);
        clienteNovo.setTelefone(dto.telefone());
        clienteRepository.save(clienteNovo);

        logger.info("Registro concluído com sucesso para: {}", dto.email());
    }

    public void logout(String email) {
        logger.info("Usuário deslogado: {}", email);
    }

}
