package com.ecodenuncia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SOLICITACOES_SENHA")
public class Solicitacao_senhas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SSE_ID")
    private Long id;

    @Column(name = "SSE_DATA_SOLICITACAO", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "SSE_DATA_EXPIRACAO", nullable = false)
    private LocalDateTime dataExpiracao;

    @Enumerated(EnumType.STRING)
    @Column(name = "SSE_STATUS", nullable = false)
    private StatusSolicitacao status;

    // Relacionamento com USUARIOS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_USUARIOS_USU_ID", nullable = false)
    private Usuario usuario;
    
    @Column(name = "SSE_TOKEN", nullable = false, unique = true, length = 100)
    private String token;

    

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(LocalDateTime dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
  

    public enum StatusSolicitacao {
        PENDENTE,
        REALIZADO
    }

}
