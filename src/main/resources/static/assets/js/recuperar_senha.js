/* --- RECUPERAR-SENHA.JS (versão corrigida e segura) --- */
document.addEventListener("DOMContentLoaded", () => {
    "use strict";
    console.log("--- SCRIPT DE RECUPERAÇÃO DE SENHA CARREGADO ---");

    // Seleciona o formulário e campos principais
    const form = document.getElementById("formRecuperarSenha");
    const email = document.getElementById("email");

    // Se o formulário ou o campo não existirem, encerra o script
    if (!form || !email) {
        console.warn("⚠️ Elementos do formulário de recuperação não encontrados no DOM.");
        return;
    }

    const btnSubmit = form.querySelector('button[type="submit"]');

    // Função auxiliar de validação simples de e-mail
    const validaEmail = () => {
        const valor = email.value.trim();
        const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!regexEmail.test(valor)) {
            email.setCustomValidity("invalid");
            form.classList.add("was-validated");
        } else {
            email.setCustomValidity("");
        }
    };

    email.addEventListener("input", validaEmail);

    // --- Evento de envio do formulário ---
    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        event.stopPropagation();

        console.log("--- EVENTO SUBMIT DISPARADO ---");

        // Valida o campo e-mail
        validaEmail();
        form.classList.add("was-validated");

        // Se o HTML reportar erro, para aqui
        if (!form.reportValidity()) {
            console.warn("⚠️ Campo de e-mail inválido.");
            return;
        }

        // Desabilita o botão e mostra spinner
        btnSubmit.disabled = true;
        btnSubmit.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Enviando...';

        try {
            const response = await fetch('/api/recuperar-senha/solicitar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email.value })
            });

            const mensagem = await response.text();

            if (response.ok) {
                swal({
                    title: "E-mail enviado!",
                    text: mensagem,
                    icon: "success",
                    button: "OK"
                }).then(() => {
                    console.log("✅ Link de recuperação enviado com sucesso!");
                    form.reset();
                    form.classList.remove("was-validated");
                });
            } else {
                swal("Erro", mensagem, "error");
            }

        } catch (error) {
            console.error("Erro na conexão com o servidor:", error);
            swal("Erro", "Falha na conexão com o servidor.", "error");

        } finally {
            btnSubmit.disabled = false;
            btnSubmit.innerHTML = 'Enviar Link de Recuperação';
        }

    }, false);
});
