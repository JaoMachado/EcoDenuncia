/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecodenuncia.controller;

import com.ecodenuncia.model.LoginResponseDTO;
import com.ecodenuncia.model.Usuario;
import com.ecodenuncia.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

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
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest, HttpServletRequest request) {
        try {
            Usuario usuario = usuarioService.autenticar(loginRequest.getEmail(), loginRequest.getSenha());

            if (usuario == null) {
                return ResponseEntity.status(401).body("Email ou senha inválidos");
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            usuario,
                            null,
                            definirPermissoes(usuario));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(context);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            return ResponseEntity.ok(new LoginResponseDTO(usuario));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao autenticar: " + e.getMessage());
        }
    }
 
    private List<SimpleGrantedAuthority> definirPermissoes(Usuario usuario) {
        String tipo = usuario.getTipoUsuario();
        String role = "ROLE_USUARIO";

        if ("A".equalsIgnoreCase(tipo)) {
            role = "ROLE_ADMIN";
        } else if ("I".equalsIgnoreCase(tipo)) {
            role = "ROLE_INSPETOR";
        }

        return List.of(new SimpleGrantedAuthority(role));
    }

}
