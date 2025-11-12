package com.ecodenuncia.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO utilizado para receber os dados que o usuário pode atualizar na tela de
 * perfil.
 */
public class UsuarioPerfilAtualizacaoDTO {

    @NotBlank(message = "Nome/Razão Social é obrigatório")
    @Size(max = 250, message = "Nome/Razão Social deve ter no máximo 250 caracteres")
    private String nomeRazaoSocial;

    @NotBlank(message = "Tipo de pessoa é obrigatório")
    @Pattern(regexp = "[FJ]", message = "Tipo de pessoa inválido")
    private String tipoPessoa;

    @NotBlank(message = "Perfil de acesso é obrigatório")
    @Pattern(regexp = "[UIA]", message = "Perfil de acesso inválido")
    private String tipoUsuario;

    @NotBlank(message = "Número de celular é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Número de celular deve conter apenas dígitos e ter 10 ou 11 números")
    private String celular;

    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 100, message = "Endereço deve ter no máximo 100 caracteres")
    private String endereco;

    @Size(max = 500, message = "Mensagem de capacitação deve ter no máximo 500 caracteres")
    private String mensagemCapacitacao;

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
}
