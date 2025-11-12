/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecodenuncia.service;

import com.ecodenuncia.model.AlterarSenhaPerfilRequest;
import com.ecodenuncia.model.Solicitacao_senhas;
import com.ecodenuncia.model.Solicitacao_senhas.StatusSolicitacao;
import com.ecodenuncia.model.Usuario;
import com.ecodenuncia.model.UsuarioPerfilAtualizacaoDTO;
import com.ecodenuncia.model.UsuarioPerfilDTO;
import com.ecodenuncia.model.UsuarioPendenteDTO;
import com.ecodenuncia.repository.UsuarioRepository;
import com.ecodenuncia.repository.SolicitacaoSenhaRepository;
import com.ecodenuncia.repository.DenunciaRepository;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    private DenunciaRepository denunciaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;


    @Transactional
    public Usuario cadastrar(Usuario usuario) {
        if (usuarioRepository.findByCpfCnpj(usuario.getCpfCnpj()).isPresent()) {
            throw new RuntimeException("Erro: CPF/CNPJ do usuário já cadastrado!");
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Erro: E-mail do usuário já cadastrado!");
        }

        // Criptografa a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        // Define o status conforme o tipo de usuário
        if ("U".equalsIgnoreCase(usuario.getTipoUsuario()) || "A".equalsIgnoreCase(usuario.getTipoUsuario())) {
            usuario.setStatusCadastro("APROVADO");
        } else {
            usuario.setStatusCadastro("PENDENTE");
        }

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
    
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            // Verifica se o cadastro foi aprovado
            if (!"APROVADO".equalsIgnoreCase(usuario.getStatusCadastro())) {
                return null; // Usuário ainda não aprovado
            }

            // Verifica se a senha digitada confere com a armazenada (hash)
            if (passwordEncoder.matches(senha, usuario.getSenha())) {
                return usuario; // Login válido
            }
        }

        return null; // Login inválido
    }



    

    @Transactional(readOnly = true)
    public UsuarioPerfilDTO buscarPerfil(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
        return new UsuarioPerfilDTO(usuario);
    }

    @Transactional
    public UsuarioPerfilDTO atualizarPerfil(Long id, UsuarioPerfilAtualizacaoDTO dados) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        String nome = dados.getNomeRazaoSocial().trim();
        if (!StringUtils.hasText(nome)) {
            throw new IllegalArgumentException("Informe um nome ou razão social válido.");
        }

        String celularApenasDigitos = somenteDigitos(dados.getCelular());
        if (celularApenasDigitos.length() < 10 || celularApenasDigitos.length() > 11) {
            throw new IllegalArgumentException("Número de celular inválido. Utilize DDD + número com 10 ou 11 dígitos.");
        }

        String endereco = dados.getEndereco().trim();
        if (!StringUtils.hasText(endereco)) {
            throw new IllegalArgumentException("O endereço é obrigatório.");
        }

        String tipoPessoa = dados.getTipoPessoa().toUpperCase();
        String tipoUsuario = dados.getTipoUsuario().toUpperCase();

        String mensagemCapacitacao = dados.getMensagemCapacitacao();
        if ("I".equals(tipoUsuario)) {
            if (!StringUtils.hasText(mensagemCapacitacao)) {
                throw new IllegalArgumentException("A mensagem de capacitação é obrigatória para perfis de Inspetor.");
            }
            if (mensagemCapacitacao.length() > 500) {
                throw new IllegalArgumentException("A mensagem de capacitação deve ter no máximo 500 caracteres.");
            }
        } else {
            mensagemCapacitacao = null;
        }

        usuario.setNomeRazaoSocial(nome);
        usuario.setTipoPessoa(tipoPessoa);
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setCelular(celularApenasDigitos);
        usuario.setEndereco(endereco);
        usuario.setMensagemCapacitacao(mensagemCapacitacao);

        Usuario salvo = usuarioRepository.save(usuario);
        return new UsuarioPerfilDTO(salvo);
    }

    @Transactional
    public void atualizarSenha(Long id, AlterarSenhaPerfilRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta.");
        }

        if (!StringUtils.hasText(request.getNovaSenha()) || request.getNovaSenha().length() < 6) {
            throw new IllegalArgumentException("A nova senha deve conter ao menos 6 caracteres.");
        }

        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void excluirPerfil(Long id, String senhaAtual) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha informada não confere com a senha atual.");
        }

        long denunciasDoUsuario = denunciaRepository.countByUsuarioDenuncianteId(id)
                + denunciaRepository.countByInspetorResponsavelId(id);

        if (denunciasDoUsuario > 0) {
            throw new IllegalArgumentException("Não é possível excluir o perfil porque existem denúncias vinculadas ao usuário.");
        }

        solicitacaoSenhaRepository.deleteByUsuarioId(id);
        usuarioRepository.delete(usuario);
    }

    private String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

   


}
