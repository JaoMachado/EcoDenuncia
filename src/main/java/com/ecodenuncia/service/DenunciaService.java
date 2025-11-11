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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import com.ecodenuncia.repository.UsuarioRepository;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author João Pedro Machado
 */

@Service
public class DenunciaService {
    @Autowired
    private DenunciaRepository denunciaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método para buscar todas e converter para DTO
    @Transactional(readOnly = true)
    public List<DenunciaDTO> buscarTodas() {
        return denunciaRepository.findAll()
                .stream()       // Transforma a lista em um stream
                .map(DenunciaDTO::new) // Converte cada Denuncia para DenunciaDTO
                .collect(Collectors.toList()); // Coleta em uma nova lista
    }

    // Método para um inspetor/gestor assumir a denúncia
    @Transactional
    public DenunciaDTO assumirDenuncia(Long id) {
        // Pega o usuário que está LOGADO no Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailDoInspetor = (String) auth.getPrincipal();
        Usuario inspetorLogado = usuarioRepository.findByEmail(emailDoInspetor)
        .orElseThrow(() -> new RuntimeException("Inspetor logado não encontrado no banco de dados!"));

        Denuncia denuncia = denunciaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Denúncia não encontrada!"));

        if (!"PENDENTE".equals(denuncia.getStatus())) {
            throw new RuntimeException("Esta denúncia não pode ser assumida!");
        }

        denuncia.setStatus("EM ANDAMENTO");
        denuncia.setInspetorResponsavel(inspetorLogado); // Seta o OBJETO do inspetor
        
        Denuncia denunciaSalva = denunciaRepository.save(denuncia);
        return new DenunciaDTO(denunciaSalva); // Retorna o DTO
    }

    // Método para criar/registrar uma nova denúncia (preenchimento do formulário)
    public DenunciaDTO criarDenuncia(String titulo, String descricao, String localizacao,
                                     String tipoProblema, MultipartFile midia, Long idUsuario, boolean anonimo) throws Exception {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Denuncia denuncia = new Denuncia();
        denuncia.setTitulo(titulo);
        denuncia.setDescricao(descricao);
        denuncia.setLocalizacao(localizacao);
        denuncia.setStatus("PENDENTE");
        denuncia.setDataCriacao(LocalDateTime.now());
        denuncia.setUsuarioDenunciante(usuario);
        denuncia.setAnonimo(anonimo);

        // Salva a URL ou nome do arquivo da mídia (caso exista)
        if (midia != null  && !midia.isEmpty()) {
            String nomeArquivo = midia.getOriginalFilename();
            // Aqui você pode salvar no servidor ou em pasta local
            denuncia.setUrlMidia(nomeArquivo);
        }

        denunciaRepository.save(denuncia);

        return new DenunciaDTO(denuncia);
    }



    // Método para concluir uma denúncia
    @Transactional
    public DenunciaDTO concluirDenuncia(Long id) {
        Denuncia denuncia = denunciaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Denúncia não encontrada!"));

        if (!"EM ANDAMENTO".equals(denuncia.getStatus())) {
            throw new RuntimeException("Esta denúncia não pode ser concluída!");
        }

        denuncia.setStatus("RESOLVIDA");
        Denuncia denunciaSalva = denunciaRepository.save(denuncia);
        return new DenunciaDTO(denunciaSalva); // Retorna o DTO
    }
}
