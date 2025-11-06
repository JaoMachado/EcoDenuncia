/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecodenuncia.controller;

import com.ecodenuncia.model.EmailRequest;
import com.ecodenuncia.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador responsável pelo fluxo de recuperação de senha:
 * 1. Envio do e-mail de recuperação
 * 2. Validação do token enviado por e-mail
 * 3. Definição da nova senha
 */
@RestController
@RequestMapping("/api/recuperar-senha")
public class RecuperarSenhaController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Passo 1 - Usuário informa o e-mail
     * Front: POST /api/recuperar-senha/solicitar
     * Corpo: { "email": "usuario@email.com" }
     */
    @PostMapping("/solicitar")
    public ResponseEntity<String> solicitarRecuperacao(@RequestBody EmailRequest request) {
        try {
            usuarioService.solicitarRecuperacaoSenha(request.getEmail());
            return ResponseEntity.ok("E-mail de recuperação enviado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("E-mail não encontrado ou erro ao enviar o link.");
        }
    }


    /**
     * Passo 2 - Validar token (opcional, pode ser usado quando abrir o link do e-mail)
     * GET /api/recuperar-senha/validar?token=xxxxx
     */
    /*
    @GetMapping("/validar")
    public ResponseEntity<String> validarToken(@RequestParam String token) {
        boolean valido = usuarioService.validarTokenRecuperacao(token);
        if (valido) {
            return ResponseEntity.ok("Token válido.");
        } else {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }
    }
    */

    /**
     * Passo 3 - Definir nova senha
     * Front: POST /api/recuperar-senha/nova
     * Corpo: { "token": "xxx", "novaSenha": "123456" }
     */
    /*
    @PostMapping("/nova")
    public ResponseEntity<String> redefinirSenha(@RequestBody NovaSenhaRequest request) {
        try {
            usuarioService.redefinirSenha(request.getToken(), request.getNovaSenha());
            return ResponseEntity.ok("Senha redefinida com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Falha ao redefinir senha: " + e.getMessage());
        }
    }

    // DTO interno simples para receber a nova senha
    public static class NovaSenhaRequest {
        private String token;
        private String novaSenha;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public String getNovaSenha() { return novaSenha; }
        public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }
    }
    */
}
