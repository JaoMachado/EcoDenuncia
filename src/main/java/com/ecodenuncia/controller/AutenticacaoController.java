package com.ecodenuncia.controller;

import com.ecodenuncia.model.LoginDTO; // Novo Import
import com.ecodenuncia.model.Usuario;
import com.ecodenuncia.service.TokenService; // Novo Import
import com.ecodenuncia.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager; // Novo Import
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Novo Import
import org.springframework.security.core.Authentication; // Novo Import
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AutenticacaoController {

    @Autowired
    private UsuarioService usuarioService;
    
    // --- NOVAS INJEÇÕES ---
    @Autowired
    private AuthenticationManager authenticationManager; // O "Autenticador" do Spring

    @Autowired
    private TokenService tokenService; // Nosso Gerador de Token
    // --- FIM DAS NOVAS INJEÇÕES ---

    /**
     * Endpoint do Caso de Uso 3.1: Fazer Cadastro
     * (Este método continua igual)
     */
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarUsuario(@Valid @RequestBody Usuario usuario) {
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
    
    // --- NOVO MÉTODO DE LOGIN (CASO DE USO 3.2) ---
    /**
     * Endpoint do Caso de Uso 3.2: Fazer Login
     * Recebe um JSON { "email": "...", "senha": "..." }
     * Retorna um JSON { "token": "..." }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO) {
        try {
            // 1. Cria o "ticket" de autenticação (email + senha)
            var authToken = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.senha());

            // 2. O Spring Security (usando o AutenticacaoService e o PasswordEncoder)
            //    valida o ticket. Se a senha estiver errada, ele lança uma exceção.
            Authentication authentication = authenticationManager.authenticate(authToken);

            // 3. Se a autenticação deu certo, gera o Token JWT
            String token = tokenService.gerarToken(authentication);
            
            // 4. Retorna o token para o front-end
            // O front-end DEVE salvar este token (ex: no localStorage)
            return ResponseEntity.ok(new TokenResponse(token));

        } catch (Exception e) {
            // Se a autenticação falhar (usuário não encontrado, senha errada)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos.");
        }
    }
    
    // Classe interna simples para formatar a resposta do token
    private record TokenResponse(String token) {}
}