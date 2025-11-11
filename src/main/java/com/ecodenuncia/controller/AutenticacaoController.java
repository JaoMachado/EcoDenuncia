/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecodenuncia.controller;

import com.ecodenuncia.model.Usuario;
import com.ecodenuncia.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author gabri
 */

//Avisa ao spring que vai receber Requisições (API) 
@RestController
@RequestMapping ("/api")
public class AutenticacaoController {
    
    
    // o "cérebro" (a lógica de serviço) que criamos
    @Autowired
    private UsuarioService usuarioService;
    
    //Mapeia este método para a URL POST /api/cadastro
    @PostMapping("/cadastro") 
    public ResponseEntity<?> cadastrarUsuario(@Valid @RequestBody Usuario usuario){
        System.out.println("--- API /API/CADASTRO FOI CHAMADA ---");
        try {
            
            System.out.println("--- TENTANDO CADASTRAR O E-MAIL: " + usuario.getEmail());
            Usuario novoUsuario = usuarioService.cadastrar(usuario);
            
            return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
            
        } catch (RuntimeException e) {
            
            System.out.println("--- ERRO NO CADASTRO: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {
        try {
            Usuario usuario = usuarioService.autenticar(loginRequest.getEmail(), loginRequest.getSenha());

            if (usuario == null) {
                return ResponseEntity.status(401).body("Email ou senha inválidos");
            }

            // Retorna um JSON sem expor a senha
            Usuario usuarioResposta = new Usuario();
            usuarioResposta.setId(usuario.getId());
            usuarioResposta.setNomeRazaoSocial(usuario.getNomeRazaoSocial());
            usuarioResposta.setEmail(usuario.getEmail());
            usuarioResposta.setTipoUsuario(usuario.getTipoUsuario());

            return ResponseEntity.ok(usuarioResposta);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao autenticar: " + e.getMessage());
        }
    }
 
}
