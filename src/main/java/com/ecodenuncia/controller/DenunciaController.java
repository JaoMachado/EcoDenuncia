/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecodenuncia.controller;

import com.ecodenuncia.model.Denuncia;
import com.ecodenuncia.model.DenunciaDTO;
import com.ecodenuncia.repository.DenunciaRepository;
import com.ecodenuncia.service.DenunciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author João Pedro Machado
 */
@RestController
@RequestMapping("/api/denuncias") // A URL base será /api/denuncias
public class DenunciaController {
    @Autowired
    private DenunciaRepository denunciaRepository;
    
    @Autowired
    private DenunciaService denunciaService;

    // ENDPOINT 1: Buscar todas as denúncias
    // JS vai chamar: GET /api/denuncias
    @GetMapping
    public List<DenunciaDTO> buscarTodasDenuncias() {
        return denunciaService.buscarTodas();
    }

    // ENDPOINT 2: Assumir uma denúncia
    // JS vai chamar: POST /api/denuncias/{id}/assumir
    @PostMapping("/{id}/assumir")
    public ResponseEntity<?> assumirDenuncia(@PathVariable Long id) {
        try {
            DenunciaDTO denunicaAtualizada = denunciaService.assumirDenuncia(id);
            return ResponseEntity.ok(denunicaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());// Retorna erro 400
        }
    }
    
    // ENDPOINT 1: Criar uma nova denúncia (preencher formulário)
    // JS / Front-end faz POST para: POST /api/denuncias  (form-data ou application/x-www-form-urlencoded)
    @PostMapping(consumes = {"multipart/form-data"})
public ResponseEntity<?> criarDenuncia(
        @RequestParam("titulo") String titulo,
        @RequestParam("descricao") String descricao,
        @RequestParam("endereco") String endereco,
        @RequestParam(value = "tipoProblema", required = false) String tipoProblema,
        @RequestParam(value = "media", required = false) MultipartFile media,
        @RequestParam("idUsuario") Long idUsuario,
        @RequestParam(value = "anonimo", required = false) boolean anonimo
) {
    try {
        DenunciaDTO criada = denunciaService.criarDenuncia(
            titulo, descricao, endereco, tipoProblema, media, idUsuario, anonimo
        );
        return ResponseEntity.status(201).body(criada);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body("Erro ao criar denúncia: " + e.getMessage());
    }
}

    // ENDPOINT 3: Concluir uma denúncia
    // JS vai chamar: POST /api/denuncias/{id}/concluir
    @PostMapping("/{id}/concluir")
    public ResponseEntity<?> concluirDenuncia(@PathVariable Long id) {
        try {
            DenunciaDTO denunicaAtualizada = denunciaService.concluirDenuncia(id);
            return ResponseEntity.ok(denunicaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Retorna erro 400
        }
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<DenunciaDTO> buscarPorId(@PathVariable Long id) {
        return denunciaRepository.findById(id)
                .map(denuncia -> ResponseEntity.ok(new DenunciaDTO(denuncia)))
                .orElse(ResponseEntity.notFound().build());
    }

}
