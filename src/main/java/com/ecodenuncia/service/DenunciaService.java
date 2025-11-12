/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
package com.ecodenuncia.service;

import com.ecodenuncia.model.Denuncia;
import com.ecodenuncia.model.DenunciaDTO;
import com.ecodenuncia.model.Usuario;
import com.ecodenuncia.repository.DenunciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DenunciaService {
    @Autowired
    private DenunciaRepository denunciaRepository;

    // Método para buscar todas e converter para DTO
    public List<DenunciaDTO> buscarTodas() {
        return denunciaRepository.findAll()
                .stream()       // Transforma a lista em um stream
                .map(DenunciaDTO::new) // Converte cada Denuncia para DenunciaDTO
                .collect(Collectors.toList()); // Coleta em uma nova lista
    }

    // Método para um inspetor/gestor assumir a denúncia
    public DenunciaDTO assumirDenuncia(Long id) {
        // Pega o usuário que está LOGADO no Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario inspetorLogado = (Usuario) auth.getPrincipal();

        Denuncia denuncia = denunciaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Denúncia não encontrada!"));

        if (!"Aguardando".equals(denuncia.getStatus())) {
            throw new RuntimeException("Esta denúncia não pode ser assumida!");
        }

        denuncia.setStatus("Em tratamento");
        denuncia.setInspetorResponsavel(inspetorLogado); // Seta o OBJETO do inspetor
        
        Denuncia denunciaSalva = denunciaRepository.save(denuncia);
        return new DenunciaDTO(denunciaSalva); // Retorna o DTO
    }

    // Método para concluir uma denúncia
    public DenunciaDTO concluirDenuncia(Long id) {
        Denuncia denuncia = denunciaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Denúncia não encontrada!"));

        if (!"Em tratamento".equals(denuncia.getStatus())) {
            throw new RuntimeException("Esta denúncia não pode ser concluída!");
        }

        denuncia.setStatus("Concluída");
        Denuncia denunciaSalva = denunciaRepository.save(denuncia);
        return new DenunciaDTO(denunciaSalva); // Retorna o DTO
    }
} */

package com.ecodenuncia.service;

import com.ecodenuncia.model.Denuncia;
import com.ecodenuncia.model.DenunciaDTO;
import com.ecodenuncia.model.Usuario;
import com.ecodenuncia.repository.DenunciaRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecodenuncia.repository.UsuarioRepository;

/**
 *
 * @author João Pedro Machado
 */

@Service
public class DenunciaService {

    private static final String STATUS_AGUARDANDO = "Aguardando";
    private static final String STATUS_EM_TRATAMENTO = "Em tratamento";
    private static final String STATUS_CONCLUIDA = "Concluída";

    private final DenunciaRepository denunciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final Path uploadDirectory;

    public DenunciaService(
            DenunciaRepository denunciaRepository,
            UsuarioRepository usuarioRepository,
            @Value("${app.upload-dir:src/main/resources/static/uploads}") String uploadDir) {
        this.denunciaRepository = denunciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException ex) {
            throw new IllegalStateException("Não foi possível preparar o diretório de uploads", ex);
        }
    }

    // Método para buscar todas e converter para DTO
    @Transactional(readOnly = true)
    public List<DenunciaDTO> buscarTodas() {
        return denunciaRepository.findAll(Sort.by(Sort.Direction.DESC, "dataCriacao"))
                .stream()       // Transforma a lista em um stream
                .map(DenunciaDTO::new) // Converte cada Denuncia para DenunciaDTO
                .collect(Collectors.toList()); // Coleta em uma nova lista
    }

    // Método para um inspetor/gestor assumir a denúncia
    @Transactional
    public DenunciaDTO assumirDenuncia(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado!");
        }

        Object principal = auth.getPrincipal();
        Usuario inspetorLogado;
        if (principal instanceof Usuario principalUsuario) {
            inspetorLogado = usuarioRepository.findById(principalUsuario.getId())
                    .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado!"));
        } else {
            String emailDoInspetor;
            if (principal instanceof UserDetails userDetails) {
                emailDoInspetor = userDetails.getUsername();
            } else if (principal instanceof String strPrincipal) {
                emailDoInspetor = strPrincipal;
            } else {
                throw new RuntimeException("Não foi possível identificar o usuário autenticado.");
            }

            if ("anonymousUser".equalsIgnoreCase(emailDoInspetor)) {
                throw new RuntimeException("Usuário não autenticado!");
            }

            inspetorLogado = usuarioRepository.findByEmail(emailDoInspetor)
                    .orElseThrow(() -> new RuntimeException("Inspetor logado não encontrado no banco de dados!"));
        }

        if (!"I".equalsIgnoreCase(inspetorLogado.getTipoUsuario())) {
            throw new RuntimeException("Somente inspetores podem assumir denúncias!");
        }

        if (!"APROVADO".equalsIgnoreCase(inspetorLogado.getStatusCadastro())) {
            throw new RuntimeException("Seu cadastro ainda não foi aprovado para assumir denúncias.");
        }

        Denuncia denuncia = denunciaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Denúncia não encontrada!"));

        Usuario inspetorAtual = denuncia.getInspetorResponsavel();
        if (inspetorAtual != null && !inspetorAtual.getId().equals(inspetorLogado.getId())) {
            throw new RuntimeException("Esta denúncia já está sendo tratada por outro inspetor");
        }

    String statusAtual = denuncia.getStatus() != null ? denuncia.getStatus().trim() : "";
    String statusPadronizado = normalizarStatus(statusAtual);

    boolean podeAssumir = "PENDENTE".equals(statusPadronizado)
        || "AGUARDANDO".equals(statusPadronizado)
        || "EM ANALISE".equals(statusPadronizado);

    if (!podeAssumir && !"EM TRATAMENTO".equals(statusPadronizado)) {
            throw new RuntimeException("Esta denúncia não pode ser assumida!");
        }

    if ("EM TRATAMENTO".equals(statusPadronizado)) {
            // Denúncia já está com este inspetor em tratamento, apenas retorna o DTO
            if (inspetorAtual != null && inspetorAtual.getId().equals(inspetorLogado.getId())) {
                return new DenunciaDTO(denuncia);
            }
            throw new RuntimeException("Esta denúncia já está sendo tratada por outro inspetor");
        }

    denuncia.setStatus(STATUS_EM_TRATAMENTO);
        denuncia.setInspetorResponsavel(inspetorLogado); // Seta o OBJETO do inspetor
        
        Denuncia denunciaSalva = denunciaRepository.save(denuncia);
        return new DenunciaDTO(denunciaSalva); // Retorna o DTO
    }

    // Método para criar/registrar uma nova denúncia (preenchimento do formulário)
    public DenunciaDTO criarDenuncia(String titulo, String descricao, String localizacao,
                                     String tipoProblema, MultipartFile midia, Long idUsuario, boolean anonimo) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Denuncia denuncia = new Denuncia();
        denuncia.setTitulo(titulo);
        denuncia.setDescricao(descricao);
        denuncia.setLocalizacao(localizacao);
        denuncia.setStatus(STATUS_AGUARDANDO);
        denuncia.setDataCriacao(LocalDateTime.now());
        denuncia.setUsuarioDenunciante(usuario);
        denuncia.setAnonimo(anonimo);

        // Salva a URL ou nome do arquivo da mídia (caso exista)
        if (midia != null && !midia.isEmpty()) {
            String caminhoMidia = salvarMidia(midia);
            denuncia.setUrlMidia(caminhoMidia);
        }

        denunciaRepository.save(denuncia);

        return new DenunciaDTO(denuncia);
    }



    // Método para concluir uma denúncia
    @Transactional
    public DenunciaDTO concluirDenuncia(Long id) {
        Denuncia denuncia = denunciaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Denúncia não encontrada!"));

        String statusPadronizado = normalizarStatus(denuncia.getStatus());
        if (!"EM TRATAMENTO".equals(statusPadronizado)) {
            throw new RuntimeException("Esta denúncia não pode ser concluída!");
        }

        denuncia.setStatus(STATUS_CONCLUIDA);
        Denuncia denunciaSalva = denunciaRepository.save(denuncia);
        return new DenunciaDTO(denunciaSalva); // Retorna o DTO
    }

    private String normalizarStatus(String status) {
        if (status == null) {
            return "";
        }
        String textoNormalizado = Normalizer.normalize(status.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return textoNormalizado.toUpperCase();
    }

    private String salvarMidia(MultipartFile midia) {
        String nomeOriginal = StringUtils.cleanPath(midia.getOriginalFilename());
        if (!StringUtils.hasText(nomeOriginal)) {
            throw new RuntimeException("Arquivo inválido recebido.");
        }

        String extensao = "";
        int ponto = nomeOriginal.lastIndexOf('.');
        if (ponto >= 0) {
            extensao = nomeOriginal.substring(ponto);
        }

        String base = ponto >= 0 ? nomeOriginal.substring(0, ponto) : nomeOriginal;
        base = Normalizer.normalize(base, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9_-]", "_");

        String nomeFinal = base + "_" + System.currentTimeMillis() + extensao.toLowerCase();
        Path destino = uploadDirectory.resolve(nomeFinal);

        try {
            Files.copy(midia.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível salvar o arquivo enviado.", ex);
        }

        return "/uploads/" + nomeFinal;
    }
}
