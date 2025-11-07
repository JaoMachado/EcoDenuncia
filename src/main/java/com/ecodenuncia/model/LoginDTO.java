package com.ecodenuncia.model;

// Usamos um 'record' (do Java 17+) que é uma forma
// simples de criar um DTO imutável.
// Ele vai receber o JSON: { "email": "...", "senha": "..." }
public record LoginDTO(String email, String senha) {
}