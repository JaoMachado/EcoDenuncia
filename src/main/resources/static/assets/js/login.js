(() => {
    "use strict";

    // 1. Seleciona os elementos do formulário
    const form = document.getElementById("loginForm");
    const emailInput = document.getElementById("email");
    const senhaInput = document.getElementById("senha");
    const btnSubmit = form.querySelector('button[type="submit"]');

    /**
     * Esta é a função principal de submit
     */
    form.addEventListener("submit", async (event) => {
        // 2. Impede o recarregamento da página
        event.preventDefault();
        event.stopPropagation();

        // 3. Aplica as classes de validação do Bootstrap
        form.classList.add("was-validated");

        // 4. Se a validação do HTML falhar (ex: e-mail em branco), para aqui
        if (!form.checkValidity()) {
            return;
        }

        // 5. Desabilita o botão para evitar cliques duplos
        btnSubmit.disabled = true;
        btnSubmit.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Logando...';

        // 6. Monta o objeto JSON (LoginDTO) para enviar ao back-end
        const loginData = {
            email: emailInput.value,
            senha: senhaInput.value
        };

        // 7. O "LINK" - Faz a chamada (fetch) para a API de Login
        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(loginData),
            });

            // 8. Trata a resposta do Back-end
            if (response.ok) { // Sucesso (HTTP 200 OK)
                
                // Pega a resposta (que é { "token": "..." })
                const data = await response.json(); 
                
                // 9. SALVA O TOKEN! Este é o passo mais importante.
                // Agora o usuário está "logado" no navegador.
                localStorage.setItem("authToken", data.token);

                // Opcional: Salvar o tipo de usuário (se o back-end enviar)
                // localStorage.setItem("userRole", data.role); 

                alert("Login realizado com sucesso!");
                window.location.href = "index.html"; // Redireciona para a página principal

            } else { // Erro (HTTP 401 Unauthorized)
                
                // Pega a mensagem de erro do back-end (ex: "E-mail ou senha inválidos.")
                const erroMsg = await response.text();
                alert(`Erro ao logar: ${erroMsg}`);
            }

        } catch (error) {
            // Erro de rede (ex: API fora do ar)
            console.error('Erro de rede:', error);
            alert('Não foi possível conectar ao servidor. Tente novamente mais tarde.');
        
        } finally {
            // 10. Reabilita o botão, independente do resultado
            btnSubmit.disabled = false;
            btnSubmit.innerHTML = '<i class="fas fa-sign-in-alt me-2"></i> Logar';
        }
    });
})();