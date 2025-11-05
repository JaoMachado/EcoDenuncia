/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecodenuncia.service;

import com.ecodenuncia.model.Usuario;
import com.ecodenuncia.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
    private PasswordEncoder passwordEncoder;

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

        usuario.setStatusCadastro("AGUARDANDO_APROVAÇÃO!!");

        return usuarioRepository.save(usuario);

    }
}
