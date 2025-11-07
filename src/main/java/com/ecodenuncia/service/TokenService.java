package com.ecodenuncia.service;

import com.ecodenuncia.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {

    // 1. Define o tempo de validade do token (ex: 2 horas)
    @Value("${api.security.token.expiration:7200000}") // Pega do application.properties ou usa 2h
    private Long expiration;

    // 2. Define a chave secreta para assinar o token
    
    // @Value("${api.security.token.secret}")
    // private String secret;
    
    // Por enquanto, vamos usar uma chave segura gerada
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Gera um novo Token JWT para o usuário autenticado.
     */
    public String gerarToken(Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        Date dataExpiracao = new Date(System.currentTimeMillis() + expiration);

        return Jwts.builder()
                .setIssuer("API EcoDenuncia") // Quem está gerando
                .setSubject(usuarioLogado.getEmail()) // Quem é o dono (o e-mail/username)
                .setIssuedAt(new Date()) // Data de geração
                .setExpiration(dataExpiracao) // Data de expiração
                .signWith(secretKey, SignatureAlgorithm.HS256) // Assina com a chave
                .compact();
    }

    /**
     * Valida o token e retorna o "Subject" (o e-mail do usuário)
     */
    public String getSubject(String tokenJWT) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(tokenJWT)
                    .getBody();
            
            return claims.getSubject(); // Retorna o e-mail

        } catch (Exception e) {
            // Se o token for inválido, expirado, etc.
            return null;
        }
    }
}