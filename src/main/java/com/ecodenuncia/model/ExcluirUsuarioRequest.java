package com.ecodenuncia.model;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilizado para validar a senha no momento da exclusão do perfil.
 */
public class ExcluirUsuarioRequest {

    @NotBlank(message = "A senha atual é obrigatória para confirmar a exclusão")
    private String senhaAtual;

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }
}
