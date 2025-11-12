package com.ecodenuncia.model;

public class LoginResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String tipoUsuario;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNomeRazaoSocial();
        this.email = usuario.getEmail();
        this.tipoUsuario = usuario.getTipoUsuario();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
