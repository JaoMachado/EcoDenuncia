// administracao.js

// Simulação de cadastros pendentes
let inspetoresPendentes = [
  { nome: 'João Silva', email: 'joao@exemplo.com', status: 'Pendente', mensagem: 'Quero ser inspetor para ajudar na fiscalização e garantir qualidade nos processos.'},
  { nome: 'Maria Souza', email: 'maria@exemplo.com', status: 'Pendente', mensagem: 'Tenho experiência na área e desejo contribuir para a melhoria dos serviços.' }
];

// Renderiza tabela de inspetores
function renderTabelaInspetores() {
  const tbody = document.querySelector('#tabelaInspetores tbody');
  tbody.innerHTML = '';

  if (inspetoresPendentes.length === 0 || !inspetoresPendentes.some(i => i.status === 'Pendente')) {
    tbody.innerHTML = `
      <tr>
        <td colspan="5" class="text-center text-muted">Nenhum cadastro pendente.</td>
      </tr>
    `;
    return;
  }

  inspetoresPendentes.forEach((inspetor, index) => {
    const row = document.createElement('tr');

    row.innerHTML = `
      <td>${inspetor.nome}</td>
      <td>${inspetor.email}</td>
      <td>${inspetor.mensagem}</td>
      <td>${inspetor.status}</td>
      <td>
        ${inspetor.status === 'Pendente' ? `
          <button class="btn btn-success btn-sm me-1" onclick="aprovarInspetor(${index})">
            Aprovar
          </button>
          <button class="btn btn-danger btn-sm" onclick="rejeitarInspetor(${index})">
            Rejeitar
          </button>
        ` : '<span class="text-muted">Analisado</span>'}
      </td>
    `;
    tbody.appendChild(row);
  });
}

// Funções de aprovação e rejeição
window.aprovarInspetor = function(idx) {
  if (inspetoresPendentes[idx].status !== 'Pendente') {
    alert('Este cadastro já foi analisado.');
    return;
  }
  inspetoresPendentes[idx].status = 'Aprovado';
  alert('Cadastro aprovado!');
  renderTabelaInspetores();
};

window.rejeitarInspetor = function(idx) {
  if (inspetoresPendentes[idx].status !== 'Pendente') {
    alert('Este cadastro já foi analisado.');
    return;
  }
  inspetoresPendentes[idx].status = 'Rejeitado';
  alert('Cadastro rejeitado! Mensagem será enviada por e-mail.');
  renderTabelaInspetores();
};

// Inicialização
$(document).ready(function () {
  renderTabelaInspetores();
});
