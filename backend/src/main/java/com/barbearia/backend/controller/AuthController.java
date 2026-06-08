package com.barbearia.backend.controller;

import com.barbearia.backend.dto.LoginRequestDTO;
import com.barbearia.backend.dto.LoginResponseDTO;
import com.barbearia.backend.dto.RegistroRequestDTO;
import com.barbearia.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto){
        LoginResponseDTO resultado = authService.login(dto);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/registro")
    public ResponseEntity<Void> registro(@RequestBody @Valid RegistroRequestDTO dto){
        authService.registro(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();


    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
