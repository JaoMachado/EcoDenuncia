/* --- Bootstrap + validações customizadas --- */

console.log("--- NOVO CADASTRO.JS CARREGADO (VERSÃO FETCH + SWAL) ---");
(() => {
    "use strict";

    // Regex CPF/CNPJ (apenas para máscara/formato, não valida dígitos aqui)
    const cpfRegex = /^\d{3}\.\d{3}\.\d{3}\-\d{2}$/;
    const cnpjRegex = /^\d{2}\.\d{3}\.\d{3}\/\d{4}\-\d{2}$/;

    // Seleciona os elementos do formulário
    const form = document.getElementById("formCadastro");
    const perfilRadios = document.getElementsByName("perfil");
    const grupoCap = document.getElementById("grupoCapacitacao");
    const capacitacao = document.getElementById("capacitacao");
    const senha = document.getElementById("senha");
    const confSenha = document.getElementById("confSenha");
    const cpfCnpj = document.getElementById("cpfCnpj");
    const nome = document.getElementById("nome");
    const tipoPessoa = document.getElementsByName("tipoPessoa");
    const email = document.getElementById("email");
    const celular = document.getElementById("celular");
    const endereco = document.getElementById("endereco");
    const btnSubmit = form.querySelector('button[type="submit"]');

    /* --- Exibir/ocultar campo Capacitação --- */
    perfilRadios.forEach(radio => {
        radio.addEventListener("change", () => {
            if (document.getElementById("inspetor").checked) {
                grupoCap.classList.remove("d-none");
                capacitacao.setAttribute("required", "required");
            } else {
                grupoCap.classList.add("d-none");
                capacitacao.removeAttribute("required");
                capacitacao.value = "";
            }
        });
    });

    /* --- Validação de Senha = Confirmar Senha --- */
    const validaSenhaIgual = () => {
        if (senha.value !== confSenha.value) {
            // Define o erro customizado (Fluxo Alternativo C)
            confSenha.setCustomValidity("mismatch");
            form.classList.add("was-validated");
        } else {
            confSenha.setCustomValidity("");
        }
    };
    senha.addEventListener("input", validaSenhaIgual);
    confSenha.addEventListener("input", validaSenhaIgual);

    /* --- Validação de CPF/CNPJ (Formato) --- */
    const validaCpfCnpj = () => {
        // Deixaremos a validação de dígitos para o back-end (Fluxo D)
        // Aqui, só checamos se o formato básico está ok (mesmo sem pontos)
        const v = cpfCnpj.value.trim();
        if (v.length >= 11) {
            cpfCnpj.setCustomValidity("");
        } else {
            cpfCnpj.setCustomValidity("invalid");
        }
    };
    cpfCnpj.addEventListener("input", validaCpfCnpj);


    /* ---
     * ESTA É A PARTE PRINCIPAL: O SUBMIT (ENVIO)
     --- */
    form.addEventListener("submit", async (event) => {
        // 1. Previne o recarregamento da página
        event.preventDefault();
        event.stopPropagation();

        console.log("--- EVENTO SUBMIT FOI DISPARADO ---");
        // 2. Roda as validações customizadas
        validaSenhaIgual();
        validaCpfCnpj();

        // 3. Adiciona as classes de validação do Bootstrap
        form.classList.add("was-validated");

        // 4. VERIFICA SE O FORMULÁRIO (HTML) É VÁLIDO
        // Se `required`, `pattern`, etc., não passaram, ele para aqui.
        if (!form.reportValidity()) {
            console.error("--- VALIDAÇÃO FALHOU: O navegador reportou um campo inválido. ---");
            return; // Para aqui
        }

        // Se chegou aqui, o formulário é VÁLIDO.
        console.log("--- VALIDAÇÃO OK! Enviando para a API... ---");

        // 5. Desabilita o botão para evitar cliques duplos
        btnSubmit.disabled = true;
        btnSubmit.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Cadastrando...';

        // 6. Monta o objeto JSON para enviar ao back-end
        // Os nomes aqui (ex: nomeRazaoSocial) DEVEM ser
        // iguais aos nomes da sua classe 'Usuario.java' no Spring.
        const usuarioData = {
            nomeRazaoSocial: nome.value,
            cpfCnpj: cpfCnpj.value.replace(/[^\d]/g, ''), // Envia só os números
            email: email.value,
            senha: senha.value,
            celular: celular.value.replace(/[^\d]/g, ''), // Envia só os números
            endereco: endereco.value,
            // Mapeia os 'name' do HTML para os campos do Java
            tipoPessoa: document.querySelector('input[name="tipoPessoa"]:checked').value,
            tipoUsuario: document.querySelector('input[name="perfil"]:checked').value,
            mensagemCapacitacao: capacitacao.value
        };

        // 7. O "LINK" - Faz a chamada (fetch) para a API Spring Boot
        try {
            const response = await fetch('/api/cadastro', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(usuarioData),
            });

            // 8. Trata a resposta do Back-end
            if (response.ok) { // Sucesso (HTTP 201 Created)

                swal({
                    title: "Cadastro Realizado!",
                    text: "Seu cadastro foi enviado. Você será redirecionado para o login.",
                    icon: "success",
                    button: "OK"
                }).then(() => {
                    // Redireciona para o login APÓS o usuário clicar em "OK"
                    window.location.href = '/login.html'; 
                });

            } else { // Erro (HTTP 400 Bad Request)

                // Pega a mensagem de erro do back-end (ex: "E-mail já cadastrado")
                const erroMsg = await response.text();
                
                // Ex: "Erro: E-mail já cadastrado"
                swal("Erro no Cadastro", erroMsg.replace("Error ", ""), "error"); 

                // Se o erro foi e-mail ou CPF, marca o campo
                if (erroMsg.includes('E-mail') || erroMsg.includes('Email')) {
                    email.setCustomValidity("invalid");
                } else if (erroMsg.includes('CPF') || erroMsg.includes('Cpf') || erroMsg.includes('Cnpj')) {
                    cpfCnpj.setCustomValidity("invalid");
                }
                form.classList.add("was-validated");
            }

        } catch (error) {
            // Erro de rede (ex: API fora do ar)
            console.error('Erro de rede:', error);
            
            swal("Erro de Conexão", "Não foi possível conectar ao servidor. Tente novamente mais tarde.", "error");

        } finally {
            // 9. Reabilita o botão, independente do resultado
            btnSubmit.disabled = false;
            btnSubmit.innerHTML = '<i class="fas fa-user-plus me-2"></i> Cadastrar';
        }

    }, false);
})();