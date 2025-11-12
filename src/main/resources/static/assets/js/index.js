document.addEventListener("DOMContentLoaded", () => {
  "use strict";

  const usuario = JSON.parse(localStorage.getItem("usuarioLogado"));

  // Seletores de elementos do topo
  const boxPerfil = document.querySelector(".topbar-user");
  const menuCadastro = document.querySelector(".menu-cadastro");
  const menuLogin = document.querySelector(".menu-login");
  const menuDenunciar = document.querySelector(".menu-denunciar");
  const menuGestao = document.querySelector(".menu-gestao");
  const menuAdministracao = document.querySelector(".menu-administracao");
  const menuEstatisticas = document.querySelector(".menu-estatisticas");

  // Elementos da home
  const inputBusca = document.getElementById("buscaId");
  const btnBusca = document.getElementById("btnBusca");
  const listaDenuncias =
    document.getElementById("listaDenuncias") ||
    document.getElementById("listaResultados");
  const mensagemVazia = document.getElementById("mensagemVazia");
  const semPost = document.getElementById("semPost");
  const textoSemPostPadrao = semPost ? semPost.textContent.trim() : "";

  let posts = [];

  // Ajusta nome e email no topo
  if (usuario && boxPerfil) {
    const nomeUsuario = usuario.nome || usuario.nomeRazaoSocial || "Usuário";
    const primeiroNome =
      nomeUsuario.split(" ").filter(Boolean)[0] || nomeUsuario;

    const dropdownNome = boxPerfil.querySelector("h4");
    const dropdownEmail = boxPerfil.querySelector(".text-muted");
    const saudacaoNome = boxPerfil.querySelector(".profile-username .fw-bold");
    const avatarImgs = boxPerfil.querySelectorAll(".avatar-img");

    if (dropdownNome) {
      dropdownNome.textContent = nomeUsuario;
    }
    if (dropdownEmail) {
      dropdownEmail.textContent = usuario.email || "";
    }
    if (saudacaoNome) {
      saudacaoNome.textContent = primeiroNome;
    }
    if (avatarImgs.length) {
      const avatarUrl = obterAvatarUsuario(usuario);
      const fallbackAvatar = gerarAvatarSvg(nomeUsuario);
      avatarImgs.forEach((img) => {
        const usarFoto = Boolean(avatarUrl);
        img.src = usarFoto ? avatarUrl : fallbackAvatar;
        img.alt = usarFoto
          ? `Foto de ${nomeUsuario}`
          : `Avatar de ${nomeUsuario}`;
      });
    }
  }

  configurarMenus(usuario);

  if (listaDenuncias) {
    carregarDenuncias();
  }

  if (inputBusca) {
    inputBusca.addEventListener("input", aplicarFiltro);
  }

  if (btnBusca) {
    btnBusca.addEventListener("click", (event) => {
      event.preventDefault();
      aplicarFiltro();
    });
  }

  configurarLogout();

  function configurarMenus(usuarioLogado) {
    if (!usuarioLogado) {
      menuCadastro?.classList.remove("d-none");
      menuLogin?.classList.remove("d-none");
      boxPerfil?.classList.add("d-none");
      menuDenunciar?.classList.add("d-none");
      menuAdministracao?.classList.add("d-none");
      menuGestao?.classList.add("d-none");
      menuEstatisticas?.classList.add("d-none");
      return;
    }

    menuCadastro?.classList.add("d-none");
    menuLogin?.classList.add("d-none");
    boxPerfil?.classList.remove("d-none");

    switch (usuarioLogado.tipoUsuario) {
      case "U":
        menuDenunciar?.classList.remove("d-none");
        menuAdministracao?.classList.add("d-none");
        menuGestao?.classList.add("d-none");
        menuEstatisticas?.classList.add("d-none");
        break;
      case "I":
        menuDenunciar?.classList.remove("d-none");
        menuAdministracao?.classList.add("d-none");
        menuGestao?.classList.remove("d-none");
        menuEstatisticas?.classList.add("d-none");
        break;
      case "A":
        menuDenunciar?.classList.remove("d-none");
        menuAdministracao?.classList.remove("d-none");
        menuGestao?.classList.remove("d-none");
        menuEstatisticas?.classList.remove("d-none");
        break;
      default:
        menuDenunciar?.classList.add("d-none");
        menuAdministracao?.classList.add("d-none");
        menuGestao?.classList.add("d-none");
        menuEstatisticas?.classList.add("d-none");
    }
  }

  async function carregarDenuncias() {
    if (!listaDenuncias) {
      return;
    }

    try {
      if (semPost) {
        semPost.textContent =
          textoSemPostPadrao || "Nenhuma denúncia disponível no momento.";
        semPost.classList.add("d-none");
      }

      listaDenuncias.innerHTML = "";
      mensagemVazia?.classList.add("d-none");

      const resposta = await fetch("/api/denuncias", {
        credentials: "include",
      });
      if (!resposta.ok) {
        throw new Error("Não foi possível carregar as denúncias.");
      }

      const dados = await resposta.json();
      if (!Array.isArray(dados) || dados.length === 0) {
        posts = [];
        if (semPost) {
          semPost.classList.remove("d-none");
        }
        swal({
          title: "Nenhuma denúncia disponível no momento.",
          icon: "info",
          buttons: false,
          timer: 2600,
        });
        return;
      }

      dados.forEach((denuncia) => {
        const elemento = criarPost(denuncia);
        listaDenuncias.appendChild(elemento);
      });

      atualizarPosts();
      aplicarFiltro();
    } catch (erro) {
      console.error("Erro ao carregar denúncias:", erro);
      if (semPost) {
        semPost.textContent =
          "Não foi possível carregar as denúncias no momento.";
        semPost.classList.remove("d-none");
      }
      swal({
        title: "Erro ao carregar denúncias",
        text: erro.message || "Tente novamente em instantes.",
        icon: "error",
      });
    }
  }

  function criarPost(denuncia) {
    const post = document.createElement("article");
    post.className =
      "feed-post d-flex flex-column flex-md-row py-4 border-bottom";
    post.dataset.id = formatarId(denuncia.id);

    const avatar = criarAvatar(denuncia.denunciante);
    const conteudo = document.createElement("div");
    conteudo.className = "flex-grow-1";

    const cabecalho = document.createElement("div");
    cabecalho.className = "d-flex align-items-center flex-wrap gap-2 mb-1";

    const nome = document.createElement("span");
    nome.className = "fw-semibold";
    nome.textContent = denuncia.denunciante || "Anônimo";
    cabecalho.appendChild(nome);

    const idBadge = document.createElement("span");
    idBadge.className = "badge bg-secondary-subtle text-secondary fw-semibold";
    idBadge.textContent = `ID #${formatarId(denuncia.id)}`;
    cabecalho.appendChild(idBadge);

    if (denuncia.status) {
      const statusBadge = document.createElement("span");
      statusBadge.className =
        "badge bg-light text-secondary text-uppercase border";
      statusBadge.textContent = denuncia.status;
      cabecalho.appendChild(statusBadge);
    }

    const dataFormatada = formatarData(denuncia.data);
    if (dataFormatada) {
      const dataElem = document.createElement("small");
      dataElem.className = "text-muted ms-auto";
      dataElem.textContent = dataFormatada;
      cabecalho.appendChild(dataElem);
    }

    conteudo.appendChild(cabecalho);

    const descricao = document.createElement("p");
    descricao.className = "mb-3";
    descricao.textContent = denuncia.mensagem || "Descrição não informada.";
    conteudo.appendChild(descricao);

    const midiaElemento = construirMidia(denuncia.midia);
    if (midiaElemento) {
      conteudo.appendChild(midiaElemento);
    }

    if (denuncia.endereco) {
      const enderecoElem = document.createElement("p");
      enderecoElem.className = "small mb-0";
      const link = document.createElement("a");
      link.href = montarLinkMaps(denuncia.endereco);
      link.target = "_blank";
      link.rel = "noopener";
      link.textContent = denuncia.endereco;
      enderecoElem.innerHTML =
        '<i class="fas fa-map-marker-alt text-danger me-1"></i>';
      enderecoElem.appendChild(link);
      conteudo.appendChild(enderecoElem);
    }

    if (usuario) {
      const botao = document.createElement("button");
      botao.type = "button";
      botao.className = "btn btn-sm btn-outline-danger mt-2 btn-denunciar";
      botao.innerHTML = '<i class="fas fa-bullhorn me-1"></i> Denunciar';
      botao.addEventListener("click", () => {
        window.location.href = `denunciar.html?referencia=${denuncia.id}`;
      });
      conteudo.appendChild(botao);
    }

    post.appendChild(avatar);
    post.appendChild(conteudo);
    return post;
  }

  function criarAvatar(nome) {
    const inicial = (nome || "A").trim().charAt(0).toUpperCase() || "A";
    const avatarContainer = document.createElement("div");
    avatarContainer.className =
      "feed-avatar flex-shrink-0 me-md-3 mb-3 mb-md-0";
    avatarContainer.innerHTML = `<div class="d-flex align-items-center justify-content-center rounded-circle bg-primary text-white fw-bold" style="width:48px;height:48px;">${inicial}</div>`;
    return avatarContainer;
  }

  function construirMidia(midia) {
    if (!midia) {
      return null;
    }

    const arquivo = midia.trim();
    if (!arquivo) {
      return null;
    }

    const wrapper = document.createElement("div");
    wrapper.className = "post-media mb-3";

    const caminho = normalizarCaminhoMidia(arquivo);

    if (/\.(mp4|webm|ogg)$/i.test(caminho.toLowerCase())) {
      const video = document.createElement("video");
      video.controls = true;
      video.className = "w-100 rounded";
      video.src = caminho;
      wrapper.appendChild(video);
    } else if (/\.(jpg|jpeg|png|gif|bmp|webp)$/i.test(caminho.toLowerCase())) {
      const img = document.createElement("img");
      img.src = caminho;
      img.alt = "Mídia anexada à denúncia";
      img.className = "img-fluid rounded";
      wrapper.appendChild(img);
    } else {
      const link = document.createElement("a");
      link.href = caminho;
      link.target = "_blank";
      link.rel = "noopener";
      link.textContent = "Abrir arquivo anexado";
      wrapper.appendChild(link);
    }

    return wrapper;
  }

  function montarLinkMaps(endereco) {
    return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(
      endereco
    )}`;
  }

  function formatarId(id) {
    if (id === undefined || id === null) {
      return "";
    }
    return String(id).padStart(5, "0");
  }

  function formatarData(dataIso) {
    if (!dataIso || typeof dataIso !== "string") {
      return "";
    }

    const [parteData, parteTempo] = dataIso.split("T");
    if (!parteData) {
      return "";
    }

    const [ano, mes, dia] = parteData.split("-");
    if (!ano || !mes || !dia) {
      return "";
    }

    let horario = "";
    if (parteTempo) {
      const [hora, minuto] = parteTempo.split(":");
      if (hora !== undefined && minuto !== undefined) {
        horario = `${hora}:${minuto}`;
      }
    }

    return `${dia}/${mes}/${ano}${horario ? ` às ${horario}` : ""}`;
  }

  function atualizarPosts() {
    posts = Array.from(listaDenuncias?.querySelectorAll(".feed-post") || []);
  }

  function aplicarFiltro() {
    if (!posts.length) {
      mensagemVazia?.classList.add("d-none");
      return;
    }

    const termo = (inputBusca?.value || "").trim();
    let algumaVisivel = false;

    posts.forEach((post) => {
      const id = post.dataset.id || "";
      if (!termo || id.includes(termo)) {
        post.classList.remove("d-none");
        algumaVisivel = true;
      } else {
        post.classList.add("d-none");
      }
    });

    if (!termo) {
      mensagemVazia?.classList.add("d-none");
      return;
    }

    mensagemVazia?.classList.toggle("d-none", algumaVisivel);
  }

  function configurarLogout() {
    const logoutBtn = document.getElementById("logoutBtn");
    if (!logoutBtn) {
      return;
    }

    logoutBtn.addEventListener("click", (event) => {
      event.preventDefault();

      swal({
        title: "Desconectar?",
        text: "Tem certeza que deseja sair?",
        icon: "warning",
        buttons: {
          cancel: "Cancelar",
          confirm: "Sair",
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
            window.location.href = "login.html";
          });

          setTimeout(() => {
            window.location.href = "login.html";
          }, 2000);
        }
      });
    });
  }

  function obterAvatarUsuario(usuarioAtual) {
    if (!usuarioAtual) {
      return null;
    }

    const camposPossiveis = [
      usuarioAtual.avatarUrl,
      usuarioAtual.fotoPerfil,
      usuarioAtual.foto,
      usuarioAtual.avatar,
      usuarioAtual.imagemPerfil,
      usuarioAtual.urlFoto,
      usuarioAtual.imagem,
    ].filter((valor) => typeof valor === "string" && valor.trim().length > 0);

    if (!camposPossiveis.length) {
      return null;
    }

    const caminho = camposPossiveis[0].trim();

    if (caminho.startsWith("http") || caminho.startsWith("data:")) {
      return caminho;
    }

    if (caminho.startsWith("/")) {
      return caminho;
    }

    if (caminho.toLowerCase().startsWith("assets/")) {
      return caminho;
    }

    if (caminho.toLowerCase().startsWith("uploads/")) {
      return `/${caminho.replace(/^\/+/, "")}`;
    }

    return `/uploads/${caminho.replace(/^\/+/, "")}`;
  }

  function gerarAvatarSvg(nomeCompleto) {
    const padrao = "U";
    const inicial =
      (nomeCompleto || "").trim().charAt(0).toUpperCase() || padrao;

    const background = selecionarCorPorNome(nomeCompleto);
    const svg = `<?xml version="1.0" encoding="UTF-8"?><svg xmlns="http://www.w3.org/2000/svg" width="96" height="96" viewBox="0 0 96 96"><rect width="96" height="96" rx="48" fill="${background}"/><text x="50%" y="50%" dy="0.35em" text-anchor="middle" fill="#ffffff" font-family="Arial, sans-serif" font-size="48">${inicial}</text></svg>`;
    return `data:image/svg+xml;base64,${btoa(
      unescape(encodeURIComponent(svg))
    )}`;
  }

  function selecionarCorPorNome(nomeCompleto) {
    if (!nomeCompleto) {
      return "#177dff";
    }

    let hash = 0;
    for (let i = 0; i < nomeCompleto.length; i += 1) {
      hash = nomeCompleto.charCodeAt(i) + ((hash << 5) - hash);
    }

    const cores = ["#177dff", "#f3545d", "#ffa534", "#31ce36", "#5c6bc0"];
    const indice = Math.abs(hash) % cores.length;
    return cores[indice];
  }

  function normalizarCaminhoMidia(valor) {
    if (!valor) {
      return "";
    }

    const caminho = valor.trim();

    if (!caminho) {
      return "";
    }

    if (caminho.startsWith("http") || caminho.startsWith("data:")) {
      return caminho;
    }

    if (caminho.startsWith("/uploads/")) {
      return caminho;
    }

    if (caminho.toLowerCase().startsWith("uploads/")) {
      return `/${caminho.replace(/^\/+/, "")}`;
    }

    return `/uploads/${caminho.replace(/^\/+/, "")}`;
  }
});
