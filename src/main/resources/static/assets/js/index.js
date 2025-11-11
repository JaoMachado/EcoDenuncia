document.addEventListener("DOMContentLoaded", () => {
    const usuario = JSON.parse(localStorage.getItem("usuarioLogado"));

    // Seletores de elementos
    const boxPerfil = document.querySelector(".topbar-user");
    const menuCadastro = document.querySelector(".menu-cadastro");
    const menuLogin = document.querySelector(".menu-login");
    const menuDenunciar = document.querySelector(".menu-denunciar");
    const menuGestao = document.querySelector(".menu-gestao");
    const menuAdministracao = document.querySelector(".menu-administracao");
    const menuEstatisticas = document.querySelector(".menu-estatisticas");

    // Ajusta nome e email no topo
    if (usuario && boxPerfil) {
        const nomeElem = boxPerfil.querySelector("h4");
        const emailElem = boxPerfil.querySelector(".text-muted");
        if (nomeElem)
            nomeElem.textContent = usuario.nome || "Usuário";
        if (emailElem)
            emailElem.textContent = usuario.email || "";
    }

    // Exibição de menus conforme o tipo de usuário
    if (!usuario) {
        // VISITANTE → Início, Boas Práticas, Sobre
        menuCadastro?.classList.remove("d-none");
        menuLogin?.classList.remove("d-none");
        boxPerfil?.classList.add("d-none");
        menuDenunciar?.classList.add("d-none");
        menuAdministracao?.classList.add("d-none");
        menuGestao?.classList.add("d-none");
        menuEstatisticas?.classList.add("d-none");
    } else if (usuario.tipoUsuario === "U") {
        // USUÁRIO → Início, Boas Práticas, Sobre, Denunciar
        menuCadastro?.classList.add("d-none");
        menuLogin?.classList.add("d-none");
        boxPerfil?.classList.remove("d-none");
        menuAdministracao?.classList.add("d-none");
        menuGestao?.classList.add("d-none");
    } else if (usuario.tipoUsuario === "I") {
        // INSPETOR → Início, Boas Práticas, Sobre, Denunciar, Gestão
        menuCadastro?.classList.add("d-none");
        menuLogin?.classList.add("d-none");
        boxPerfil?.classList.remove("d-none");
        menuAdministracao?.classList.add("d-none");
    } else if (usuario.tipoUsuario === "A") {
        // ADMIN → Tudo
        menuCadastro?.classList.add("d-none");
        menuLogin?.classList.add("d-none");
        boxPerfil?.classList.remove("d-none");
        menuAdministracao?.classList.remove("d-none");
        menuDenunciar?.classList.remove("d-none");
        menuGestao?.classList.remove("d-none");
        menuAdministracao?.classList.remove("d-none");
        menuEstatisticas?.classList.remove("d-none");
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

    // --- Logout (Padronizado com SweetAlert) ---
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            
            swal({
                title: "Desconectar?",
                text: "Tem certeza que deseja sair?",
                icon: "warning",
                buttons: {
                    cancel: "Cancelar",
                    confirm: "Sair"
                },
            }).then((vaiSair) => {
                if (vaiSair) {
                    localStorage.removeItem("usuarioLogado");
                    
                    swal({
                        title: "Desconectado!",
                        text: "Logout realizado com sucesso!",
                        icon: "success",
                        timer: 2000,
                        buttons: false,
                    }).then(() => {
                         window.location.href = "login.html"; // Manda pro Login
                    });
                    
                    // Garante o redirecionamento
                    setTimeout(() => { window.location.href = "login.html"; }, 2000);
                }
            });
        });
    }
});
