/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecodenuncia.service;


import com.ecodenuncia.model.Solicitacao_senhas;
import com.ecodenuncia.repository.SolicitacaoSenhaRepository;
import com.ecodenuncia.repository.UsuarioRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SenhaService {

    @Autowired
    private SolicitacaoSenhaRepository solicitacaoSenhaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void alterarSenha(String token, String novaSenha) {
        var solicitacao = solicitacaoSenhaRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (!"PENDENTE".equalsIgnoreCase(solicitacao.getStatus().name())) {
            throw new RuntimeException("Token já usado ou inválido");
        }

        if (solicitacao.getDataExpiracao().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        var usuario = solicitacao.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        solicitacao.setStatus(Solicitacao_senhas.StatusSolicitacao.REALIZADO);
        solicitacaoSenhaRepository.save(solicitacao);
    }
}

