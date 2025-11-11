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
    public ResponseEntity<DenunciaDTO> assumirDenuncia(@PathVariable Long id) {
        try {
            DenunciaDTO denunicaAtualizada = denunciaService.assumirDenuncia(id);
            return ResponseEntity.ok(denunicaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // Retorna erro 400
        }
    }

    // ENDPOINT 3: Concluir uma denúncia
    // JS vai chamar: POST /api/denuncias/{id}/concluir
    @PostMapping("/{id}/concluir")
    public ResponseEntity<DenunciaDTO> concluirDenuncia(@PathVariable Long id) {
        try {
            DenunciaDTO denunicaAtualizada = denunciaService.concluirDenuncia(id);
            return ResponseEntity.ok(denunicaAtualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // Retorna erro 400
        }
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<DenunciaDTO> buscarPorId(@PathVariable Long id) {
        return denunciaRepository.findById(id)
                .map(denuncia -> ResponseEntity.ok(new DenunciaDTO(denuncia)))
                .orElse(ResponseEntity.notFound().build());
    }

}
