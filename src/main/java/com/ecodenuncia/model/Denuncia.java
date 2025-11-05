package com.ecodenuncia.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table (name = "tb_Denuncias")
public class Denuncia {
	
	@Id //Define Id como chave primaria
	//Define que o id sera gerado pelo banco.
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	
	@NotBlank(message = "Título é obrigatório")
        private String titulo;
	
	@NotBlank(message = "Descrição é obrigatoria.")
	//Defome o valor maximo pra 500.
	@Column(length = 500)
	private String descricao;

	@NotBlank(message = "Localização é obrigatória")
        private String localizacao;
	
	private String urlMidia;
	
	private String status;
	
	@Column(updatable = false) // Não deve ser atualizado depois de criado
        private LocalDateTime dataCriacao;
	
	//Relacionamento Muitos para um
	//Muitas Denuncias Podem ser feito por um usuario
	
	@ManyToOne(fetch = FetchType.LAZY)
	// JPA: Define qual coluna em "tb_denuncias" é a chave estrangeira
	@JoinColumn(name = "usuario_denunciante_id", nullable = false) 
    private Usuario usuarioDenunciante;
	
	// (MUITAS Denúncias podem ser assumidas por UM Inspetor)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspetor_responsavel_id") // (Pode ser nulo no início)
    private Usuario inspetorResponsavel;

    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getUrlMidia() {
        return urlMidia;
    }

    public void setUrlMidia(String urlMidia) {
        this.urlMidia = urlMidia;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Usuario getUsuarioDenunciante() {
        return usuarioDenunciante;
    }

    public void setUsuarioDenunciante(Usuario usuarioDenunciante) {
        this.usuarioDenunciante = usuarioDenunciante;
    }

    public Usuario getInspetorResponsavel() {
        return inspetorResponsavel;
    }

    public void setInspetorResponsavel(Usuario inspetorResponsavel) {
        this.inspetorResponsavel = inspetorResponsavel;
    }
	
    
	

}
