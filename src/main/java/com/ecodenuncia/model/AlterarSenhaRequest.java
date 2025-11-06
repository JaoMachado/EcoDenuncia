/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecodenuncia.model;

import java.time.LocalDateTime;

/**
 *
 * @author João Pedro Machado
 */
public class DenunciaDTO {
    private Long id;
    private String titulo;
    private String endereco; // No JS você chama de 'endereco', na Entidade é 'localizacao'
    private LocalDateTime data; // No JS é 'data', na Entidade é 'dataCriacao'
    private String status;
    private String inspetor; // No JS é 'inspetor', na Entidade é 'inspetorResponsavel' (um Objeto)

    // Construtor para facilitar a conversão
    public DenunciaDTO(Denuncia denuncia) {
        this.id = denuncia.getId();
        this.titulo = denuncia.getTitulo();
        this.endereco = denuncia.getLocalizacao(); // De/Para
        this.data = denuncia.getDataCriacao();     // De/Para
        this.status = denuncia.getStatus();
        
        // Lógica para pegar só o NOME do inspetor
        if (denuncia.getInspetorResponsavel() != null) {
            this.inspetor = denuncia.getInspetorResponsavel().getNomeRazaoSocial();
        } else {
            this.inspetor = null; // Ou "-"
        }
    }
    
    // Getters e Setters para todos os campos...
    // (O Jackson, que converte para JSON, precisa dos getters)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getInspetor() { return inspetor; }
    public void setInspetor(String inspetor) { this.inspetor = inspetor; }
}
