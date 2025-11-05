$(document).ready(function () {
    $('#formExcluirPerfil').on('submit', function (event) {
      event.preventDefault(); // Impede envio automático
      event.stopPropagation();

      const senha = $('#senhaConfirmacaoExclusao').val().trim();

      if (senha === '') {
        $('#senhaConfirmacaoExclusao').addClass('is-invalid');
        return;
      } else {
        $('#senhaConfirmacaoExclusao').removeClass('is-invalid');
      }

      // Simulação de exclusão com redirecionamento
      alert('Conta excluída com sucesso!');
      localStorage.removeItem("tipoUsuario"); // simula exclusão);
      window.location.href = "index.html"; // Vai para a página inicial
    });
});
