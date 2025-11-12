(() => {
  "use strict";

  const formPerfil = document.getElementById("formPerfil");
  const fieldsetPerfil = document.getElementById("perfilFieldset");
  const btnEditar = document.getElementById("btnEditar");
  const btnSalvar = document.getElementById("btnSalvar");
  const btnCancelar = document.getElementById("btnCancelar");
  const linkSolicitarSenha = document.getElementById("linkSolicitarSenha");
  const formExcluirPerfil = document.getElementById("formExcluirPerfil");
  const modalExcluirPerfil = document.getElementById("confirmDeleteModal");
  const inputSenhaExclusao = document.getElementById(
    "senhaConfirmacaoExclusao"
  );
  const alertaErroExclusao = document.getElementById("erroSenhaExclusao");
  const btnConfirmarExclusao = document.getElementById("btnConfirmarExclusao");
  const formMudarSenha = document.getElementById("formMudarSenha");
  const modalAlterarSenha = document.getElementById("changePasswordModal");
  const erroConfirmacaoSenha = document.getElementById("erroConfirmacaoSenha");
  const campoCapacitacao = document.getElementById("capacitacao");
  const grupoCapacitacao = document.querySelector(".grupo-capacitacao");
  const campoCelular = document.getElementById("celular");
  const campoAdministrador = document.getElementById("administrador");
  const blocoAdministrador = document.getElementById("opcaoAdministrador");
  const textoOriginalSalvar = btnSalvar ? btnSalvar.innerHTML : "";
  const textoOriginalExcluir = btnConfirmarExclusao
    ? btnConfirmarExclusao.innerHTML
    : "";

  let usuarioLogado = JSON.parse(localStorage.getItem("usuarioLogado"));
  let perfilAtual = null;
  let emEdicao = false;
  let mapaPerfil = null;
  let geocoder = null;
  let marcadorUsuario = null;
  let marcadoresDenuncias = [];
  let coordenadaUsuario = null;
  let denunciasCarregadas = false;

  window.initMap = function initMap() {
    mapaPerfil = new google.maps.Map(document.getElementById("map"), {
      zoom: 12,
      center: { lat: -23.5505, lng: -46.6333 },
      mapTypeControl: false,
      streetViewControl: false,
    });
    geocoder = new google.maps.Geocoder();

    if (perfilAtual) {
      posicionarUsuarioNoMapa();
    } else if (!denunciasCarregadas) {
      carregarDenunciasProximas();
    }
  };

  if (!usuarioLogado || !usuarioLogado.id) {
    swal({
      title: "Sessão expirada",
      text: "Faça login novamente para acessar seu perfil.",
      icon: "warning",
    }).then(() => {
      window.location.href = "login.html";
    });

    setTimeout(() => {
      window.location.href = "login.html";
    }, 2500);
    return;
  }

  carregarPerfil();
  configurarEventos();

  function configurarEventos() {
    btnEditar?.addEventListener("click", () => habilitarEdicao(true));
    btnCancelar?.addEventListener("click", () => {
      if (perfilAtual) {
        preencherFormulario(perfilAtual);
      }
      habilitarEdicao(false);
    });

    formPerfil?.addEventListener("submit", salvarPerfil);

    document.querySelectorAll('input[name="perfil"]').forEach((radio) => {
      radio.addEventListener("change", atualizarVisibilidadeCapacitacao);
    });

    campoCelular?.addEventListener("input", () => {
      campoCelular.value = formatarCelular(campoCelular.value);
    });

    linkSolicitarSenha?.addEventListener("click", solicitarMudancaSenha);
    formExcluirPerfil?.addEventListener("submit", excluirPerfil);
    formMudarSenha?.addEventListener("submit", alterarSenha);
  }

  async function carregarPerfil() {
    try {
      const resposta = await fetch(`/api/usuarios/${usuarioLogado.id}`);
      if (!resposta.ok) {
        const mensagem = await resposta.text();
        throw new Error(mensagem || "Não foi possível carregar seus dados");
      }

      const dados = await resposta.json();
      perfilAtual = { ...dados };

      preencherFormulario(perfilAtual);
      atualizarUsuarioNoStorage(perfilAtual);
      posicionarUsuarioNoMapa();
    } catch (erro) {
      console.error("Erro ao carregar perfil:", erro);
      swal({
        title: "Erro ao abrir perfil",
        text: erro.message || "Tente novamente mais tarde.",
        icon: "error",
      }).then(() => {
        window.location.href = "index.html";
      });
    }
  }

  function preencherFormulario(dados) {
    const nome = document.getElementById("nome");
    const cpfCnpj = document.getElementById("cpfCnpj");
    const email = document.getElementById("email");
    const celular = document.getElementById("celular");
    const endereco = document.getElementById("endereco");

    if (nome) {
      nome.value = dados.nomeRazaoSocial || "";
    }
    if (cpfCnpj) {
      cpfCnpj.value = formatarCpfCnpj(dados.cpfCnpj);
    }
    if (email) {
      email.value = dados.email || "";
    }
    if (celular) {
      celular.value = formatarCelular(dados.celular);
    }
    if (endereco) {
      endereco.value = dados.endereco || "";
    }

    if (campoCapacitacao) {
      campoCapacitacao.value = dados.mensagemCapacitacao || "";
    }

    selecionarRadio('input[name="tipoPessoa"]', dados.tipoPessoa || "F");
    selecionarPerfil(dados.tipoUsuario || "U");
    atualizarVisibilidadeCapacitacao();
  }

  function selecionarRadio(query, valor) {
    const radio = document.querySelector(`${query}[value="${valor}"]`);
    if (radio) {
      radio.checked = true;
    }
  }

  function selecionarPerfil(valor) {
    if (valor === "A") {
      if (blocoAdministrador) {
        blocoAdministrador.classList.remove("d-none");
      }
      if (campoAdministrador) {
        campoAdministrador.checked = true;
        campoAdministrador.disabled = true;
      }
    } else if (blocoAdministrador) {
      blocoAdministrador.classList.add("d-none");
      if (campoAdministrador) {
        campoAdministrador.checked = false;
      }
    }

    const radio = document.querySelector(
      `input[name="perfil"][value="${valor}"]`
    );
    if (radio) {
      radio.checked = true;
    }
  }

  function habilitarEdicao(ativo) {
    emEdicao = ativo;

    if (!fieldsetPerfil) {
      return;
    }

    fieldsetPerfil.disabled = !ativo;

    const camposSomenteLeitura = [
      document.getElementById("cpfCnpj"),
      document.getElementById("email"),
      document.getElementById("senha"),
      document.getElementById("confirmarSenha"),
    ];

    camposSomenteLeitura.forEach((campo) => {
      if (!campo) {
        return;
      }
      campo.readOnly = true;
      campo.disabled = true;
    });

    if (campoAdministrador) {
      campoAdministrador.disabled = true;
    }

    btnEditar?.classList.toggle("d-none", ativo);
    btnSalvar?.classList.toggle("d-none", !ativo);
    btnCancelar?.classList.toggle("d-none", !ativo);

    if (!ativo) {
      formPerfil?.classList.remove("was-validated");
    }
  }

  async function salvarPerfil(evento) {
    evento.preventDefault();
    evento.stopPropagation();

    if (!formPerfil) {
      return;
    }

    formPerfil.classList.add("was-validated");

    if (!formPerfil.checkValidity()) {
      return;
    }

    const tipoPessoaSelecionado = document.querySelector(
      'input[name="tipoPessoa"]:checked'
    );
    const perfilSelecionado = document.querySelector(
      'input[name="perfil"]:checked'
    );

    const payload = {
      nomeRazaoSocial: (document.getElementById("nome")?.value || "").trim(),
      tipoPessoa: tipoPessoaSelecionado ? tipoPessoaSelecionado.value : "F",
      tipoUsuario: perfilSelecionado ? perfilSelecionado.value : "U",
      celular: somenteDigitos(document.getElementById("celular")?.value || ""),
      endereco: (document.getElementById("endereco")?.value || "").trim(),
      mensagemCapacitacao: (campoCapacitacao?.value || "").trim(),
    };

    if (payload.tipoUsuario !== "I") {
      payload.mensagemCapacitacao = "";
    }

    try {
      if (btnSalvar) {
        btnSalvar.disabled = true;
        btnSalvar.innerHTML =
          '<span class="spinner-border spinner-border-sm me-1"></span>Salvando...';
      }
      const resposta = await fetch(`/api/usuarios/${usuarioLogado.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (!resposta.ok) {
        const mensagem = await resposta.text();
        throw new Error(mensagem || "Não foi possível salvar as alterações.");
      }

      const atualizado = await resposta.json();
      perfilAtual = { ...atualizado };

      preencherFormulario(perfilAtual);
      atualizarUsuarioNoStorage(perfilAtual);
      habilitarEdicao(false);
      denunciasCarregadas = false;
      posicionarUsuarioNoMapa();

      swal({
        title: "Perfil atualizado",
        text: "As informações foram salvas com sucesso.",
        icon: "success",
      });
    } catch (erro) {
      console.error("Erro ao salvar perfil:", erro);
      swal({
        title: "Erro ao salvar",
        text: erro.message || "Verifique os dados e tente novamente.",
        icon: "error",
      });
    } finally {
      if (btnSalvar) {
        btnSalvar.disabled = false;
        btnSalvar.innerHTML = textoOriginalSalvar;
      }
    }
  }

  async function solicitarMudancaSenha(evento) {
    evento.preventDefault();

    if (!perfilAtual || !perfilAtual.email) {
      swal(
        "Ops",
        "Não foi possível localizar o seu e-mail cadastrado.",
        "warning"
      );
      return;
    }

    const confirmar = await swal({
      title: "Enviar link de alteração?",
      text: `Um e-mail será enviado para ${perfilAtual.email}.`,
      icon: "info",
      buttons: ["Cancelar", "Enviar"],
    });

    if (!confirmar) {
      return;
    }

    try {
      const resposta = await fetch("/api/recuperar-senha/solicitar", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email: perfilAtual.email }),
      });

      if (!resposta.ok) {
        const mensagem = await resposta.text();
        throw new Error(mensagem || "Não foi possível enviar o e-mail agora.");
      }

      swal({
        title: "E-mail enviado!",
        text: "Confira sua caixa de entrada para redefinir a senha.",
        icon: "success",
      });
    } catch (erro) {
      console.error("Erro ao solicitar mudança de senha:", erro);
      swal({
        title: "Erro",
        text: erro.message || "Não foi possível enviar o e-mail agora.",
        icon: "error",
      });
    }
  }

  async function excluirPerfil(evento) {
    evento.preventDefault();
    evento.stopPropagation();

    formExcluirPerfil?.classList.add("was-validated");
    alertaErroExclusao?.classList.add("d-none");

    if (!formExcluirPerfil || !formExcluirPerfil.checkValidity()) {
      return;
    }

    const senha = (inputSenhaExclusao?.value || "").trim();

    if (!senha) {
      return;
    }

    try {
      if (btnConfirmarExclusao) {
        btnConfirmarExclusao.disabled = true;
        btnConfirmarExclusao.innerHTML =
          '<span class="spinner-border spinner-border-sm me-1"></span>Excluindo...';
      }
      const resposta = await fetch(`/api/usuarios/${usuarioLogado.id}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ senhaAtual: senha }),
      });

      if (!resposta.ok) {
        const mensagem = await resposta.text();
        throw new Error(mensagem || "Não foi possível excluir o perfil agora.");
      }

      const modal = bootstrap.Modal.getOrCreateInstance(modalExcluirPerfil);
      modal.hide();

      localStorage.removeItem("usuarioLogado");

      swal({
        title: "Perfil excluído",
        text: "Esperamos revê-lo em breve.",
        icon: "success",
      }).then(() => {
        window.location.href = "index.html";
      });
    } catch (erro) {
      console.error("Erro ao excluir perfil:", erro);
      alertaErroExclusao?.classList.remove("d-none");
      alertaErroExclusao.textContent =
        erro.message || "Senha incorreta. Tente novamente.";
    } finally {
      if (btnConfirmarExclusao) {
        btnConfirmarExclusao.disabled = false;
        btnConfirmarExclusao.innerHTML = textoOriginalExcluir;
      }
      if (inputSenhaExclusao) {
        inputSenhaExclusao.value = "";
      }
      formExcluirPerfil?.classList.remove("was-validated");
      formExcluirPerfil?.reset();
    }
  }

  async function alterarSenha(evento) {
    evento.preventDefault();
    evento.stopPropagation();

    if (!formMudarSenha) {
      return;
    }

    formMudarSenha.classList.add("was-validated");

    if (!formMudarSenha.checkValidity()) {
      return;
    }

    const senhaAtual = (
      document.getElementById("senhaAtual")?.value || ""
    ).trim();
    const novaSenha = (
      document.getElementById("novaSenha")?.value || ""
    ).trim();
    const confirmaSenha = (
      document.getElementById("confNovaSenha")?.value || ""
    ).trim();

    if (novaSenha !== confirmaSenha) {
      erroConfirmacaoSenha?.classList.remove("d-none");
      return;
    }

    erroConfirmacaoSenha?.classList.add("d-none");

    let btnSubmit = null;
    let textoOriginal = "";

    try {
      btnSubmit = formMudarSenha.querySelector('button[type="submit"]');
      textoOriginal = btnSubmit ? btnSubmit.innerHTML : "";
      if (btnSubmit) {
        btnSubmit.disabled = true;
        btnSubmit.innerHTML =
          '<span class="spinner-border spinner-border-sm me-1"></span>Salvando...';
      }

      const resposta = await fetch(`/api/usuarios/${usuarioLogado.id}/senha`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          senhaAtual,
          novaSenha,
        }),
      });

      if (!resposta.ok) {
        const mensagem = await resposta.text();
        throw new Error(mensagem || "Não foi possível alterar a senha agora.");
      }

      const modal = bootstrap.Modal.getOrCreateInstance(modalAlterarSenha);
      modal.hide();

      formMudarSenha.reset();
      formMudarSenha.classList.remove("was-validated");

      swal({
        title: "Senha atualizada",
        text: "Sua senha foi alterada com sucesso.",
        icon: "success",
      });
    } catch (erro) {
      console.error("Erro ao alterar senha:", erro);
      swal({
        title: "Erro ao alterar senha",
        text: erro.message || "Verifique os dados informados.",
        icon: "error",
      });
    } finally {
      if (btnSubmit) {
        btnSubmit.disabled = false;
        btnSubmit.innerHTML = textoOriginal;
      }
    }
  }

  function atualizarVisibilidadeCapacitacao() {
    const perfilSelecionado = document.querySelector(
      'input[name="perfil"]:checked'
    );
    if (!perfilSelecionado || !grupoCapacitacao) {
      return;
    }

    if (perfilSelecionado.value === "I") {
      grupoCapacitacao.classList.remove("d-none");
      campoCapacitacao?.setAttribute("required", "required");
    } else {
      grupoCapacitacao.classList.add("d-none");
      campoCapacitacao?.removeAttribute("required");
    }
  }

  function atualizarUsuarioNoStorage(dados) {
    usuarioLogado = {
      ...usuarioLogado,
      nome: dados.nomeRazaoSocial,
      nomeRazaoSocial: dados.nomeRazaoSocial,
      tipoUsuario: dados.tipoUsuario,
      email: dados.email,
    };
    localStorage.setItem("usuarioLogado", JSON.stringify(usuarioLogado));
  }

  function formatarCpfCnpj(valor) {
    const digitos = somenteDigitos(valor);

    if (digitos.length === 11) {
      return digitos.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
    }

    if (digitos.length === 14) {
      return digitos.replace(
        /(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/,
        "$1.$2.$3/$4-$5"
      );
    }

    return valor || "";
  }

  function formatarCelular(valor) {
    const digitos = somenteDigitos(valor);

    if (digitos.length === 0) {
      return "";
    }

    if (digitos.length <= 10) {
      return digitos.replace(
        /(\d{0,2})(\d{0,4})(\d{0,4})/,
        (match, ddd, parte1, parte2) => {
          let resultado = "";
          if (ddd) {
            resultado += ddd;
          }
          if (parte1) {
            resultado += (resultado ? " " : "") + parte1;
          }
          if (parte2) {
            resultado += (parte2 ? "-" : "") + parte2;
          }
          return resultado;
        }
      );
    }

    return digitos.replace(
      /(\d{0,2})(\d{0,5})(\d{0,4})/,
      (match, ddd, parte1, parte2) => {
        let resultado = "";
        if (ddd) {
          resultado += ddd;
        }
        if (parte1) {
          resultado += (resultado ? " " : "") + parte1;
        }
        if (parte2) {
          resultado += (parte2 ? "-" : "") + parte2;
        }
        return resultado;
      }
    );
  }

  function posicionarUsuarioNoMapa() {
    if (!mapaPerfil || !geocoder) {
      return;
    }

    if (!perfilAtual || !perfilAtual.endereco) {
      carregarDenunciasProximas();
      return;
    }

    geocoder.geocode({ address: perfilAtual.endereco }, (results, status) => {
      if (status === "OK" && results[0]) {
        coordenadaUsuario = results[0].geometry.location;
        mapaPerfil.setCenter(coordenadaUsuario);
        mapaPerfil.setZoom(13);
        adicionarMarcadorUsuario(coordenadaUsuario);
      }
      carregarDenunciasProximas();
    });
  }

  function adicionarMarcadorUsuario(local) {
    if (!mapaPerfil) {
      return;
    }

    if (marcadorUsuario) {
      marcadorUsuario.setMap(null);
    }

    marcadorUsuario = new google.maps.Marker({
      position: local,
      map: mapaPerfil,
      title: "Minha localização",
      icon: {
        path: google.maps.SymbolPath.CIRCLE,
        scale: 8,
        fillColor: "#2f89fc",
        fillOpacity: 0.9,
        strokeColor: "#ffffff",
        strokeWeight: 2,
      },
    });
  }

  async function carregarDenunciasProximas() {
    if (denunciasCarregadas) {
      return;
    }

    denunciasCarregadas = true;

    try {
      const resposta = await fetch("/api/denuncias");
      if (!resposta.ok) {
        throw new Error("Não foi possível carregar as denúncias para o mapa.");
      }

      const denuncias = await resposta.json();
      await plotarDenunciasNoMapa(Array.isArray(denuncias) ? denuncias : []);
    } catch (erro) {
      console.error("Erro ao carregar denúncias para o mapa:", erro);
      denunciasCarregadas = false;
    }
  }

  async function plotarDenunciasNoMapa(denuncias) {
    if (!mapaPerfil || !geocoder || !Array.isArray(denuncias)) {
      return;
    }

    limparMarcadoresDenuncias();

    const limite = 15;
    const promessas = denuncias
      .slice(0, limite)
      .map((denuncia) => geocodarDenuncia(denuncia));
    const resultados = await Promise.all(promessas);

    resultados
      .filter((item) => item && item.location)
      .forEach((resultado) =>
        adicionarMarcadorDenuncia(resultado.denuncia, resultado.location)
      );
  }

  function geocodarDenuncia(denuncia) {
    return new Promise((resolve) => {
      if (!denuncia || !denuncia.endereco) {
        resolve(null);
        return;
      }

      geocoder.geocode({ address: denuncia.endereco }, (results, status) => {
        if (status === "OK" && results[0]) {
          resolve({ denuncia, location: results[0].geometry.location });
        } else {
          resolve(null);
        }
      });
    });
  }

  function adicionarMarcadorDenuncia(denuncia, location) {
    if (!mapaPerfil) {
      return;
    }

    let distanciaKm = null;
    if (
      coordenadaUsuario &&
      google.maps.geometry &&
      google.maps.geometry.spherical
    ) {
      const distanciaMetros =
        google.maps.geometry.spherical.computeDistanceBetween(
          coordenadaUsuario,
          location
        );
      distanciaKm = (distanciaMetros / 1000).toFixed(1);

      if (distanciaMetros > 25000) {
        return;
      }
    }

    const marker = new google.maps.Marker({
      position: location,
      map: mapaPerfil,
      title: denuncia.titulo || `Denúncia ${denuncia.id}`,
    });

    const infoWindow = new google.maps.InfoWindow({
      content: `
        <div style="max-width: 220px;">
          <strong>${denuncia.titulo || "Denúncia"}</strong><br/>
          <span>${denuncia.endereco || "Endereço não informado"}</span><br/>
          ${
            distanciaKm
              ? `<small class="text-muted">Aprox. ${distanciaKm} km de você</small><br/>`
              : ""
          }
          <a href="denuncia_detalhes.html?id=${
            denuncia.id
          }" class="btn btn-sm btn-primary mt-2">Ver detalhes</a>
        </div>
      `,
    });

    marker.addListener("click", () => {
      infoWindow.open(mapaPerfil, marker);
    });

    marcadoresDenuncias.push(marker);
  }

  function limparMarcadoresDenuncias() {
    marcadoresDenuncias.forEach((marker) => marker.setMap(null));
    marcadoresDenuncias = [];
  }

  function somenteDigitos(valor) {
    return (valor || "").replace(/\D/g, "");
  }
})();
