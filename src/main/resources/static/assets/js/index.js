document.addEventListener("DOMContentLoaded", () => {
    // Simulação do tipo de usuário (pode ser "Visitante", "Usuario", "Inspetor", "Administrador")
    const tipoUsuario = localStorage.getItem("tipoUsuario");

    const boxPerfil = document.querySelector(".topbar-user");
    const menuCadastro = document.querySelector(".menu-cadastro");
    const menuLogin = document.querySelector(".menu-login");
    const menuDenunciar = document.querySelector(".menu-denunciar");
    const menuAdministracao = document.querySelector(".menu-administracao");
    const menuGestao = document.querySelector(".menu-gestao");
    const menuEstatisticas = document.querySelector(".menu-estatisticas");
    const btnDenunciar = document.querySelector(".btn-denunciar");
    const btnDenunciar1 = document.querySelector(".btn-denunciar1");

    if (!tipoUsuario || tipoUsuario === "Visitante") {
      // Visitante:
        menuCadastro?.classList.remove("d-none");
        menuLogin?.classList.remove("d-none");
        boxPerfil?.classList.add("d-none");
        menuDenunciar?.classList.add("d-none");
        menuAdministracao?.classList.add("d-none");
        menuGestao?.classList.add("d-none");
        menuEstatisticas?.classList.add("d-none");
        btnDenunciar?.classList.add("d-none");
        btnDenunciar1?.classList.add("d-none");
    } else if(tipoUsuario === "Usuario") {
      // Usuário:
        menuCadastro?.classList.add("d-none");
        menuLogin?.classList.add("d-none");
        boxPerfil?.classList.remove("d-none");
        btnDenunciar?.classList.remove("d-none");
        btnDenunciar1?.classList.add("d-none");
        menuAdministracao?.classList.add("d-none");
        menuGestao?.classList.add("d-none");
    } else if(tipoUsuario === "Inspetor") {
      // Inspetor:
        menuCadastro?.classList.add("d-none");
        menuLogin?.classList.add("d-none");
        btnDenunciar?.classList.remove("d-none");
        btnDenunciar1?.classList.add("d-none");
        boxPerfil?.classList.remove("d-none");
        menuAdministracao?.classList.add("d-none");
    } else if(tipoUsuario === "Administrador") {
        btnDenunciar?.classList.remove("d-none");
        btnDenunciar1?.classList.add("d-none");
        menuCadastro?.classList.add("d-none");
        menuLogin?.classList.add("d-none");
        boxPerfil?.classList.remove("d-none");
        menuAdministracao?.classList.remove("d-none");
    }

    // Filtro
    const inputBusca = document.getElementById("buscaId");
    const posts = document.querySelectorAll(".feed-post");
    const mensagem = document.getElementById("mensagemVazia");

    inputBusca.addEventListener("input", function () {
      const termo = inputBusca.value.trim();
      let algumaVisivel = false;

      posts.forEach((post) => {
        const id = post.getAttribute("data-id") || "";

        if (id.includes(termo)) {
          post.classList.remove("d-none");
          algumaVisivel = true;
        } else {
          post.classList.add("d-none");
        }
      });

      // Mostrar ou ocultar a mensagem de "nenhuma denúncia"
      if (algumaVisivel) {
        mensagem.classList.add("d-none");
      } else {
        mensagem.classList.remove("d-none");
      }
    });

    // Simular Logout
    const logoutBtn = document.getElementById("logoutBtn");
      if(logoutBtn){
        logoutBtn.addEventListener("click", function(e){
          e.preventDefault();
          localStorage.removeItem("tipoUsuario"); // simula logout
          alert("Logout realizado com sucesso!");
          window.location.href = "index.html"; // redireciona para login
        });
      }
});
