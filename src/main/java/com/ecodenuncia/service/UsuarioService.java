/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecodenuncia.service;

import com.ecodenuncia.model.Solicitacao_senhas;
import com.ecodenuncia.model.Solicitacao_senhas.StatusSolicitacao;
import com.ecodenuncia.model.Usuario;
import com.ecodenuncia.model.UsuarioPendenteDTO;
import com.ecodenuncia.repository.UsuarioRepository;
import com.ecodenuncia.repository.SolicitacaoSenhaRepository;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author gabri
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private SolicitacaoSenhaRepository solicitacaoSenhaRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;


    @Transactional
    public Usuario cadastrar(Usuario usuario) {
        if (usuarioRepository.findByCpfCnpj(usuario.getCpfCnpj()).isPresent()) {
            throw new RuntimeException("Error Cpf/cnpj do usuario já cadastrado!!");
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Error Email do usuario já cadastrado!!");
        } //Sistema de criptografar senha
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        usuario.setStatusCadastro("PENDENTE");

        return usuarioRepository.save(usuario);

    }
    
    /**
     * Este é o método que o Spring Security (Passo 2) usa
     * para encontrar um usuário pelo 'username' (que nós definimos como e-mail).
     */
    public Usuario loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com e-mail: " + email));
    }
    
    // --- MÉTODOS DA PÁGINA ADMIN (FALTANTES) ---

    /**
     * Busca todos os usuários com status "Pendente" e converte para DTO.
     */
    public List<UsuarioPendenteDTO> buscarUsuariosPendentes() {
        List<Usuario> usuariosPendentes = usuarioRepository.findByStatusCadastro("PENDENTE");
        
        return usuariosPendentes.stream()
                .map(UsuarioPendenteDTO::new) // Converte cada Usuario para DTO
                .collect(Collectors.toList());
    }

    /**
     * Aprova um usuário, mudando seu status para "APROVADO".
     * (O "APROVADO" é o que o isEnabled() do Usuario.java entende)
     */
    @Transactional
    public Usuario aprovarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
            
        usuario.setStatusCadastro("APROVADO"); // Muda o status
        return usuarioRepository.save(usuario);
    }

    /**
     * Rejeita (exclui) um usuário do banco.
     */
    @Transactional
    public void rejeitarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
             throw new RuntimeException("Usuário não encontrado!");
        }
        usuarioRepository.deleteById(id);
    }
    @Transactional
    public void solicitarRecuperacaoSenha(String email) {
        // 1️⃣ Verifica se o usuário existe
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        // 2️⃣ Gera um token único
        String token = UUID.randomUUID().toString();

        // 3️⃣ Verifica se já existe um token igual (raro, mas seguro)
        while (solicitacaoSenhaRepository.existsByToken(token)) {
            token = UUID.randomUUID().toString();
        }

        // 4️⃣ Cria uma nova solicitação de senha
        Solicitacao_senhas solicitacao = new Solicitacao_senhas();
        solicitacao.setUsuario(usuario);
        solicitacao.setToken(token);
        solicitacao.setDataSolicitacao(LocalDateTime.now());
        solicitacao.setDataExpiracao(LocalDateTime.now().plusHours(1)); // expira em 1h
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);

        solicitacaoSenhaRepository.save(solicitacao);

        // 5️⃣ Envia o e-mail com o link de recuperação
        // Exemplo de link: http://localhost:8080/cadastrar_nova_senha.html?token=abc123
        emailService.mandar_email(usuario.getEmail(), token);
    }

   


}
