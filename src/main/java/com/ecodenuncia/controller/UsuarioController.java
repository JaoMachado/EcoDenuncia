package com.ecodenuncia.controller;

import com.ecodenuncia.model.AlterarSenhaPerfilRequest;
import com.ecodenuncia.model.ExcluirUsuarioRequest;
import com.ecodenuncia.model.UsuarioPerfilAtualizacaoDTO;
import com.ecodenuncia.model.UsuarioPerfilDTO;
import com.ecodenuncia.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints relacionados às operações da página de perfil do usuário.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPerfil(@PathVariable Long id) {
        try {
            UsuarioPerfilDTO perfil = usuarioService.buscarPerfil(id);
            return ResponseEntity.ok(perfil);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPerfil(@PathVariable Long id,
            @Valid @RequestBody UsuarioPerfilAtualizacaoDTO dados) {
        try {
            UsuarioPerfilDTO atualizado = usuarioService.atualizarPerfil(id, dados);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}/senha")
    public ResponseEntity<?> atualizarSenha(@PathVariable Long id,
            @Valid @RequestBody AlterarSenhaPerfilRequest request) {
        try {
            usuarioService.atualizarSenha(id, request);
            return ResponseEntity.ok("Senha atualizada com sucesso.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirPerfil(@PathVariable Long id,
            @Valid @RequestBody ExcluirUsuarioRequest request) {
        try {
            usuarioService.excluirPerfil(id, request.getSenhaAtual());
            return ResponseEntity.ok("Perfil excluído com sucesso.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
