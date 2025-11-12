package com.ecodenuncia.model;

/**
 * DTO utilizado para expor dados do usuário na tela de perfil sem retornar
 * informações sensíveis como a senha.
 */
public class UsuarioPerfilDTO {

    private Long id;
    private String nomeRazaoSocial;
    private String tipoPessoa;
    private String tipoUsuario;
    private String cpfCnpj;
    private String email;
    private String celular;
    private String endereco;
    private String mensagemCapacitacao;
    private String statusCadastro;

    public UsuarioPerfilDTO() {
        // Construtor padrão necessário para serialização JSON.
    }

    public UsuarioPerfilDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nomeRazaoSocial = usuario.getNomeRazaoSocial();
        this.tipoPessoa = usuario.getTipoPessoa();
        this.tipoUsuario = usuario.getTipoUsuario();
        this.cpfCnpj = usuario.getCpfCnpj();
        this.email = usuario.getEmail();
        this.celular = usuario.getCelular();
        this.endereco = usuario.getEndereco();
        this.mensagemCapacitacao = usuario.getMensagemCapacitacao();
        this.statusCadastro = usuario.getStatusCadastro();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getMensagemCapacitacao() {
        return mensagemCapacitacao;
    }

    public void setMensagemCapacitacao(String mensagemCapacitacao) {
        this.mensagemCapacitacao = mensagemCapacitacao;
    }

    public String getStatusCadastro() {
        return statusCadastro;
    }

    public void setStatusCadastro(String statusCadastro) {
        this.statusCadastro = statusCadastro;
    }
}
