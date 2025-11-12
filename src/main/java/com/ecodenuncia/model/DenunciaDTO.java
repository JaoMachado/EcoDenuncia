/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecodenuncia.model;

import java.time.LocalDateTime;

import org.springframework.util.StringUtils;

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
    private String denunciante;
    private String mensagem;
    private String midia;
    private boolean anonimo;

    // Construtor para facilitar a conversão
    public DenunciaDTO(Denuncia denuncia) {
        this.id = denuncia.getId();
        this.titulo = denuncia.getTitulo();
        this.endereco = denuncia.getLocalizacao(); // De/Para
        this.data = denuncia.getDataCriacao();     // De/Para
        this.status = denuncia.getStatus();
        this.mensagem = denuncia.getDescricao();
    this.midia = resolverUrlMidia(denuncia.getUrlMidia());
        this.anonimo = denuncia.isAnonimo();
        
        // Lógica para pegar só o NOME do inspetor
        if (denuncia.getInspetorResponsavel() != null) {
            this.inspetor = denuncia.getInspetorResponsavel().getNomeRazaoSocial();
        } else {
            this.inspetor = null; // Ou "-"
        }

        if (this.anonimo) {
            this.denunciante = "Anônimo";
        } else if (denuncia.getUsuarioDenunciante() != null) {
            this.denunciante = denuncia.getUsuarioDenunciante().getNomeRazaoSocial();
        } else {
            this.denunciante = "";
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
    public String getDenunciante() { return denunciante; }
    public void setDenunciante(String denunciante) { this.denunciante = denunciante; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public String getMidia() { return midia; }
    public void setMidia(String midia) { this.midia = midia; }
    public boolean isAnonimo() { return anonimo; }
    public void setAnonimo(boolean anonimo) { this.anonimo = anonimo; }

    private String resolverUrlMidia(String urlMidia) {
        if (!StringUtils.hasText(urlMidia)) {
            return null;
        }

        String valor = urlMidia.trim();
        if (valor.startsWith("http") || valor.startsWith("data:")) {
            return valor;
        }

        if (valor.startsWith("/uploads/")) {
            return valor;
        }

        if (valor.toLowerCase().startsWith("uploads/")) {
            return "/" + valor.replaceAll("^/+", "");
        }

        return "/uploads/" + valor.replaceAll("^/+", "");
    }
}
