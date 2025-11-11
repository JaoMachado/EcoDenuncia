document.addEventListener("DOMContentLoaded", () => {
  "use strict";

  const form = document.getElementById("formDenuncia");
  
  // Se não estiver na página de denúncia, não faz nada
  if (!form) {
    return;
  }

  const btnSubmit = form.querySelector('button[type="submit"]');
  const textoBotaoOriginal = btnSubmit.innerHTML;

  form.addEventListener("submit", async (event) => {
    event.preventDefault();

    // Feedback visual (Spinner)
    btnSubmit.disabled = true;
    btnSubmit.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Enviando...';

    const usuarioLogado = JSON.parse(localStorage.getItem("usuarioLogado") || "{}");
    
    // 1. Alerta "Feio" (Login) - Substituído
    if (!usuarioLogado.id) {
      swal("Acesso Negado", "Faça login antes de registrar uma denúncia!", "error");
      btnSubmit.disabled = false;
      btnSubmit.innerHTML = textoBotaoOriginal;
      return;
    }

    const formData = new FormData(form);
    formData.append("idUsuario", usuarioLogado.id);

    try {
      const resposta = await fetch("http://localhost:8080/api/denuncias", {
        method: "POST",
        body: formData
      });

      if (resposta.ok) {
        // 2. Alerta "Feio" (Sucesso) - Substituído
        swal({
            title: "Denúncia Registrada!",
            text: "Sua denúncia foi enviada com sucesso e será analisada.",
            icon: "success",
            button: "OK"
        }).then(() => {
            form.reset();
            window.location.href = "index.html"; // Redireciona para a home
        });

      } else {
        // 3. Alerta "Feio" (Erro do Servidor) - Substituído
        const erro = await resposta.text();
        swal("Erro ao Enviar", erro.replace("Erro ao criar denúncia: ", ""), "error");
      }
    } catch (erro) {
      // 4. Alerta "Feio" (Erro de Rede) - Substituído
      console.error("Erro:", erro);
      swal("Erro de Conexão", "Falha ao conectar com o servidor.", "error");
    
    } finally {
      // Reabilita o botão (exceto em caso de sucesso, pois redireciona)
      if (!btnSubmit.disabled) { // Se o 'catch' rodou
         btnSubmit.disabled = false;
         btnSubmit.innerHTML = textoBotaoOriginal;
      }
    }
  });
});