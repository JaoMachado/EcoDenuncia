// Este array agora vai funcionar como um "cache" dos dados vindos do banco
let denuncias = [];

// Função para formatar a data (o banco manda LocalDateTime, precisamos de "dd/mm/aaaa")
function formatarData(dataISO) {
    if (!dataISO) return '-';
    // Converte a data ISO (ex: "2025-11-05T18:30:00") para um objeto Data do JS
    const data = new Date(dataISO); 
    // Formata para o padrão brasileiro
    return data.toLocaleDateString('pt-BR'); 
}

// A função de renderizar a tabela continua quase igual,
// só que agora ela lê do nosso array 'denuncias' (que será preenchido pela API)
function renderTabela(filtro = {}) {
    const tbody = document.querySelector('#tabelaDenuncias tbody');
    tbody.innerHTML = '';

    const filtradas = denuncias.filter(d => {
        if (filtro.status && d.status !== filtro.status) return false;
        // Filtro de data: compara apenas a parte da data (ignora horas)
        if (filtro.data && !d.data.startsWith(filtro.data)) return false; 
        if (filtro.local && !d.endereco.toLowerCase().includes(filtro.local.toLowerCase())) return false;
        return true;
    });

    const mensagem = document.getElementById('mensagemSemDenuncia');
    if (filtradas.length === 0) {
        mensagem?.classList.remove('d-none');
    } else {
        mensagem?.classList.add('d-none');
    }

    filtradas.forEach(d => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${d.id}</td>
            <td>${d.titulo}</td>
            <td>${d.endereco}</td>
            <td>${formatarData(d.data)}</td> <td>${d.status}</td>
            <td>${d.inspetor || '-'}</td>
            <td>
                ${d.status === 'Aguardando' ? `<button class="btn btn-sm btn-success" onclick="assumirDenuncia('${d.id}')">Assumir</button>` : ''}
                ${d.status === 'Em tratamento' ? `<button class="btn btn-sm btn-primary" onclick="concluirDenuncia('${d.id}')">Concluir</button>` : ''}
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// O formulário de filtro agora só precisa re-renderizar
// os dados que já estão no cache 'denuncias'.
$('#filtroForm').on('submit', function(e) {
    e.preventDefault();
    renderTabela({
        status: $('#filtroStatus').val(),
        data: $('#filtroData').val(),
        local: $('#filtroLocal').val()
    });
});

// <<< MUDANÇA: Função 'assumir' agora chama a API
window.assumirDenuncia = async function(id) {
    try {
        const response = await fetch(`/api/denuncias/${id}/assumir`, {
            method: 'POST'
        });

        if (!response.ok) {
            throw new Error('Não foi possível assumir a denúncia.');
        }

        alert('Denúncia assumida com sucesso!');
        // Atualiza a lista inteira do banco para garantir
        await carregarDenuncias(); 

    } catch (error) {
        alert(error.message);
    }
};

// <<< MUDANÇA: Função 'concluir' agora chama a API
window.concluirDenuncia = async function(id) {
    try {
        const response = await fetch(`/api/denuncias/${id}/concluir`, {
            method: 'POST'
        });

        if (!response.ok) {
            throw new Error('Não foi possível concluir a denúncia.');
        }

        alert('Denúncia marcada como concluída!');
        // Atualiza a lista inteira do banco
        await carregarDenuncias();

    } catch (error) {
        alert(error.message);
    }
};

// <<< MUDANÇA: Nova função para carregar dados da API
async function carregarDenuncias() {
    try {
        const response = await fetch('/api/denuncias');
        if (!response.ok) {
            throw new Error('Falha ao carregar denúncias.');
        }
        denuncias = await response.json(); // Salva os dados no nosso cache global
        renderTabela(); // Renderiza a tabela com os dados do banco
    } catch (error) {
        console.error(error);
        alert('Erro ao carregar dados do servidor.');
    }
}

// <<< MUDANÇA: Quando a página carregar, chama a função da API
$(document).ready(function() {
    carregarDenuncias();
});