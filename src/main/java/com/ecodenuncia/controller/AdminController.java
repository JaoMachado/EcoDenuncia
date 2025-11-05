/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecodenuncia.controller;
import com.ecodenuncia.model.UsuarioPendenteDTO;
import com.ecodenuncia.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author João Pedro Machado
 */

@RestController
@RequestMapping("/api/admin") // URL base para tudo de admin
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    // ENDPOINT 1: Buscar usuários pendentes
    // JS vai chamar: GET /api/admin/pendentes
    @GetMapping("/pendentes")
    public List<UsuarioPendenteDTO> getUsuariosPendentes() {
        return usuarioService.buscarUsuariosPendentes();
    }
    
    // ENDPOINT 2: Aprovar um usuário
    // JS vai chamar: POST /api/admin/aprovar/{id}
    @PostMapping("/aprovar/{id}")
    public ResponseEntity<Void> aprovarUsuario(@PathVariable Long id) {
        try {
            usuarioService.aprovarUsuario(id);
            return ResponseEntity.ok().build(); // Retorna 200 OK (Vazio)
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404
        }
    }
    
    // ENDPOINT 3: Rejeitar um usuário
    // JS vai chamar: POST /api/admin/rejeitar/{id} (Usamos POST por segurança)
    @PostMapping("/rejeitar/{id}")
    public ResponseEntity<Void> rejeitarUsuario(@PathVariable Long id) {
        try {
            usuarioService.rejeitarUsuario(id);
            return ResponseEntity.ok().build(); // Retorna 200 OK (Vazio)
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404
        }
    }
}
