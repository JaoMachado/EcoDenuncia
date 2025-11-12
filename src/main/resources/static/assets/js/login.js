(() => {
  "use strict";

  const form = document.getElementById("loginForm");
  const emailInput = document.getElementById("email");
  const senhaInput = document.getElementById("senha");
  // Tenta pegar o botão de submit para dar feedback visual
  const btnSubmit = form.querySelector('button[type="submit"]');

  form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const email = emailInput.value.trim();
    const senha = senhaInput.value.trim();

    if (!email || !senha) {
      swal("Atenção", "Por favor, preencha todos os campos.", "warning");
      return;
    }

    // Feedback visual de carregamento
    const textoOriginal = btnSubmit.innerHTML;
    btnSubmit.disabled = true;
    btnSubmit.innerHTML =
      '<span class="spinner-border spinner-border-sm me-2"></span>Entrando...';

    try {
      const response = await fetch("/api/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // <<< ADICIONADO para sessão Spring
        body: JSON.stringify({ email, senha }),
      });

      if (!response.ok) {
        // Tenta ler a mensagem de erro do servidor, ou usa uma genérica
        const err = await response.text();
        throw new Error(err || "E-mail ou senha incorretos.");
      }

      const usuario = await response.json();

      if (!usuario.nomeRazaoSocial && usuario.nome) {
        usuario.nomeRazaoSocial = usuario.nome;
      }

      // Armazena o objeto completo do usuário
      localStorage.setItem("usuarioLogado", JSON.stringify(usuario));

      // Sucesso! Redireciona após o usuário clicar em OK
      swal({
        title: "Bem-vindo(a)!",
        text: `Login realizado com sucesso, ${
          usuario.nome || usuario.nomeRazaoSocial || "usuário"
        }.`,
        icon: "success",
        timer: 2000, // Opcional: fecha sozinho em 2s se o usuário não clicar
        buttons: false, // Esconde o botão se usar timer, ou use button: "OK"
      }).then(() => {
        window.location.href = "index.html";
      });

      // Se usar o timer, força o redirecionamento após o tempo
      setTimeout(() => {
        window.location.href = "index.html";
      }, 2000);
    } catch (error) {
      console.error("Erro no login:", error);
      // Mostra o erro (seja do servidor ou de conexão)
      swal(
        "Erro no Login",
        error.message || "Erro de conexão com o servidor.",
        "error"
      );

      // Reabilita o botão apenas em caso de erro
      btnSubmit.disabled = false;
      btnSubmit.innerHTML = textoOriginal;
    }
  });
})();
