package com.barbearia.backend.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String gerarToken(String email) {
        return Jwts.builder()
                .subject(email)                                    // quem é o dono do token
                .issuedAt(new Date())                              // quando foi gerado
                .expiration(new Date(System.currentTimeMillis() + expiration)) // quando expira
                .signWith(getSigningKey())                         // assina com nossa chave secreta
                .compact();                                        // gera a String final
    }

    public String extrairEmail(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())        // verifica a assinatura
                .build()
                .parseSignedClaims(token)           // lê o conteúdo do token
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValido(String token, String email){

        String emailDoToken = extrairEmail(token);
        return emailDoToken.equals(email) && !isTokenExpirado(token);
    }

    private boolean isTokenExpirado(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
