package com.ecodenuncia.model;

/**
 *
 * @author João Pedro Machado
 */

// Este DTO vai carregar SÓ os dados que a sua página de admin precisa
public class UsuarioPendenteDTO {

    private Long id;
    private String nome; // (vem de nomeRazaoSocial)
    private String email;
    private String mensagemCapacitacao;
    private String status; // (vem de statusCadastro)

    // Construtor que faz a "tradução"
    public UsuarioPendenteDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNomeRazaoSocial();
        this.email = usuario.getEmail();
        this.mensagemCapacitacao = usuario.getMensagemCapacitacao();
        this.status = usuario.getStatusCadastro();
    }

    // Getters (necessários para o Spring converter em JSON)
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getMensagemCapacitacao() { return mensagemCapacitacao; }
    public String getStatus() { return status; }
}