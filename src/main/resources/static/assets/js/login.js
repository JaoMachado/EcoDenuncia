(() => {
  "use strict";

  const form = document.getElementById("loginForm");
  const emailInput = document.getElementById("email");
  const senhaInput = document.getElementById("senha");

  // Credenciais fictícias para simulação
  const USUARIO_DEMO = {
    email: "usuario@gmail.com",
    senha: "teste"
  };

  form.addEventListener("submit", (event) => {
    // Impede envio do form por padrão
    event.preventDefault();
    event.stopPropagation();

    // Aplica classe do Bootstrap para feedback visual
    form.classList.add("was-validated");

    const email = emailInput.value.trim();
    const senha = senhaInput.value;

    // Fluxo Alternativo B: Usuário não existe
    if (email !== USUARIO_DEMO.email) {
      alert("O e-mail informado não pertence a nenhuma conta. Deseja criar uma nova?");
      return;
    }

    // Fluxo Alternativo C: Senha incorreta
    if (senha !== USUARIO_DEMO.senha) {
      alert("Senha incorreta. Tente novamente.");
      return;
    }

    // Sucesso (Fluxo Principal)
    localStorage.setItem("tipoUsuario", "Usuario");
    alert("Login realizado com sucesso!");
    window.location.href = "index.html"; // Redireciona para página principal
  });
})();
