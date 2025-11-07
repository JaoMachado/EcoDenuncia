package com.ecodenuncia.service;

import com.ecodenuncia.model.Usuario;
import com.ecodenuncia.model.UsuarioPendenteDTO; // Nova importação
import com.ecodenuncia.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import java.util.List; // Nova importação
import java.util.stream.Collectors; // Nova importação
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
        
        // 1. Validação de duplicados (como já estava)
        if (usuarioRepository.findByCpfCnpj(usuario.getCpfCnpj()).isPresent()) {
            throw new RuntimeException("Erro: Cpf/cnpj do usuario já cadastrado!!");
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Erro: Email do usuario já cadastrado!!");
        }
        
        // 2. Criptografar senha (como já estava)
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        
        
        
        // 3. Mapeia o Tipo de Usuário (Front-end envia 'U' ou 'I')
        String tipoUsuarioForm = usuario.getTipoUsuario();
        
        if ("I".equals(tipoUsuarioForm)) {
            // Se for INSPETOR
            usuario.setTipoUsuario("INSPETOR");
            // Define o status para verificação (Pedido do usuário)
            usuario.setStatusCadastro("AGUARDANDO_APROVACAO"); 
        } else {
            // Se for USUARIO normal
            usuario.setTipoUsuario("USUARIO");
            // Define o status como APROVADO direto (Pedido do usuário)
            usuario.setStatusCadastro("APROVADO"); 
        }
        
        // 4. Mapeia o Tipo de Pessoa (Front-end envia 'F' ou 'J')
        String tipoPessoaForm = usuario.getTipoPessoa();
        if ("F".equals(tipoPessoaForm)) {
            usuario.setTipoPessoa("PESSOA_FISICA");
        } else {
            usuario.setTipoPessoa("PESSOA_JURIDICA");
        }
        
        

        // 5. Salva o novo usuário no banco
        return usuarioRepository.save(usuario);
    }
    
    // --- INÍCIO DOS NOVOS MÉTODOS PARA O ADMINCONTROLLER ---
    
    /**
     * Busca todos os usuários com status AGUARDANDO_APROVACAO
     * (Converte para DTO)
     */
    @Transactional
    public List<UsuarioPendenteDTO> buscarUsuariosPendentes() {
        return usuarioRepository.findAll().stream()
                // Filtra apenas os usuários que estão aguardando
                .filter(usuario -> "AGUARDANDO_APROVACAO".equals(usuario.getStatusCadastro()))
                // Converte a Entidade Usuario para o DTO UsuarioPendenteDTO
                .map(UsuarioPendenteDTO::new) 
                .collect(Collectors.toList());
    }

    /**
     * Aprova o cadastro de um usuário (muda o status)
     */
    @Transactional
    public void aprovarUsuario(Long id) {
        // Busca o usuário no banco ou lança um erro se não existir
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        
        // Altera o status
        usuario.setStatusCadastro("APROVADO");
        
        // Salva a alteração
        usuarioRepository.save(usuario);
    }

    /**
     * Rejeita o cadastro de um usuário (muda o status)
     */
    @Transactional
    public void rejeitarUsuario(Long id) {
        // Busca o usuário no banco ou lança um erro se não existir
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        
        // Altera o status para REJEITADO
        usuario.setStatusCadastro("REJEITADO");
        
        // Salva a alteração
        usuarioRepository.save(usuario);
    }
    // --- FIM DOS NOVOS MÉTODOS ---
}