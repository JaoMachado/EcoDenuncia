/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 *
 * @author João Pedro Machado
 */

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
}
