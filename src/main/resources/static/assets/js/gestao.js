// gestao.js
// Simulação de dados 

let denuncias = [
  {
    id: '000231',
    titulo: 'Vazamento forte de cano',
    endereco: 'Rua das Palmeiras, 530 – São Paulo - SP',
    data: '2025-06-28',
    status: 'Aguardando',
    inspetor: ''
  },
  {
    id: '000232',
    titulo: 'Peixes mortos no lago',
    endereco: 'Parque Verde – Setor Norte',
    data: '2025-06-27',
    status: 'Em tratamento',
    inspetor: 'Gabriel'
  }
];

function renderTabela(filtro = {}) {
  const tbody = document.querySelector('#tabelaDenuncias tbody');
  tbody.innerHTML = '';

  const filtradas = denuncias.filter(d => {
    if (filtro.status && d.status !== filtro.status) return false;
    if (filtro.data && d.data !== filtro.data) return false;
    if (filtro.local && !d.endereco.toLowerCase().includes(filtro.local.toLowerCase())) return false;
    return true;
  });

  const mensagem = document.getElementById('mensagemSemDenuncia');
  if (filtradas.length === 0) {
    mensagem?.classList.remove('d-none');
    return;
  } else {
    mensagem?.classList.add('d-none');
  }

  filtradas.forEach(d => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${d.id}</td>
      <td>${d.titulo}</td>
      <td>${d.endereco}</td>
      <td>${d.data}</td>
      <td>${d.status}</td>
      <td>${d.inspetor || '-'}</td>
      <td>
        ${d.status === 'Aguardando' ? `<button class="btn btn-sm btn-success" onclick="assumirDenuncia('${d.id}')">Assumir</button>` : ''}
        ${d.status === 'Em tratamento' ? `<button class="btn btn-sm btn-primary" onclick="concluirDenuncia('${d.id}')">Concluir</button>` : ''}
      </td>
    `;
    tbody.appendChild(tr);
  });
}

$('#filtroForm').on('submit', function(e) {
  e.preventDefault();
  renderTabela({
    status: $('#filtroStatus').val(),
    data: $('#filtroData').val(),
    local: $('#filtroLocal').val()
  });
});

window.assumirDenuncia = function(id) {
  const denuncia = denuncias.find(d => d.id === id);
  if (!denuncia || denuncia.status !== 'Aguardando') {
    alert('Esta denúncia não pode ser assumida.');
    return;
  }
  denuncia.status = 'Em tratamento';
  denuncia.inspetor = 'Inspetor padrão'; // Ou você pode deixar vazio
  alert('Denúncia assumida com sucesso!');
  renderTabela();
};

window.concluirDenuncia = function(id) {
  const denuncia = denuncias.find(d => d.id === id);
  if (!denuncia || denuncia.status !== 'Em tratamento') {
    alert('Esta denúncia não pode ser concluída.');
    return;
  }
  denuncia.status = 'Concluída';
  alert('Denúncia marcada como concluída!');
  renderTabela();
};

$(document).ready(function() {
  renderTabela();
});
