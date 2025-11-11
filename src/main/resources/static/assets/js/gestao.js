// Este array agora vai funcionar como um "cache" dos dados vindos do banco
let denuncias = [];

// Função para formatar a data (o banco manda LocalDateTime, precisamos de "dd/mm/aaaa")
function formatarData(dataISO) {
    if (!dataISO) return '-';
    const data = new Date(dataISO); 
    return data.toLocaleDateString('pt-BR'); 
}

// A função de renderizar a tabela (Não precisa de mudanças)
function renderTabela(filtro = {}) {
    const tbody = document.querySelector('#tabelaDenuncias tbody');
    tbody.innerHTML = '';

    const filtradas = denuncias.filter(d => {
        if (filtro.status && d.status !== filtro.status) return false;
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
            <td>${formatarData(d.data)}</td>
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

// O formulário de filtro (Não precisa de mudanças)
$('#filtroForm').on('submit', function(e) {
    e.preventDefault();
    renderTabela({
        status: $('#filtroStatus').val(),
        data: $('#filtroData').val(),
        local: $('#filtroLocal').val()
    });
});

/**
 * <<< MUDANÇA: Função 'assumir' agora usa swal() para confirmar
 */
window.assumirDenuncia = function(id) {
    swal({
        title: "Assumir Denúncia?",
        text: "Você será marcado como o inspetor responsável por este caso.",
        icon: "info",
        buttons: {
            cancel: "Cancelar",
            confirm: "Assumir"
        },
    }).then(async (vaiAssumir) => {
        if (vaiAssumir) {
            try {
                const response = await fetch(`/api/denuncias/${id}/assumir`, {
                    method: 'POST'
                });

                if (response.ok) {
                    swal("Sucesso!", "Denúncia assumida com sucesso!", "success");
                    await carregarDenuncias(); // Recarrega a tabela
                } else {
                    // Tenta ler a mensagem de erro do back-end
                    const erroMsg = await response.text();
                    swal("Erro", erroMsg || "Não foi possível assumir a denúncia.", "error");
                }

            } catch (error) {
                console.error(error);
                swal("Erro de Conexão", "Não foi possível conectar ao servidor.", "error");
            }
        }
    });
};

/**
 * <<< MUDANÇA: Função 'concluir' agora usa swal() para confirmar
 */
window.concluirDenuncia = function(id) {
    swal({
        title: "Concluir Denúncia?",
        text: "Tem certeza que deseja marcar esta denúncia como 'Concluída'?",
        icon: "warning",
        buttons: {
            cancel: "Cancelar",
            confirm: "Concluir"
        },
    }).then(async (vaiConcluir) => {
        if (vaiConcluir) {
            try {
                const response = await fetch(`/api/denuncias/${id}/concluir`, {
                    method: 'POST'
                });

                if (response.ok) {
                    swal("Concluída!", "Denúncia marcada como concluída!", "success");
                    await carregarDenuncias(); // Recarrega a tabela
                } else {
                    const erroMsg = await response.text();
                    swal("Erro", erroMsg || "Não foi possível concluir a denúncia.", "error");
                }
            } catch (error) {
                console.error(error);
                swal("Erro de Conexão", "Não foi possível conectar ao servidor.", "error");
            }
        }
    });
};

/**
 * <<< MUDANÇA: Função de carregar agora usa swal() para erros
 */
async function carregarDenuncias() {
    try {
        const response = await fetch('/api/denuncias');
        if (!response.ok) {
            // Se der erro 403 (Proibido), é o Spring Security bloqueando
            if(response.status === 403) {
                 swal("Acesso Negado", "Você não tem permissão de Gestor ou Admin para ver esta página.", "error");
            }
            throw new Error('Falha ao carregar denúncias. Status: ' + response.status);
        }
        denuncias = await response.json(); // Salva os dados no cache global
        renderTabela(); // Renderiza a tabela com os dados do banco
    } catch (error) {
        console.error(error);
        // Evita mostrar o alerta de permissão duas vezes
        if (!error.message.includes('403')) {
             swal('Erro ao Carregar', 'Erro ao carregar dados do servidor.', 'error');
        }
    }
}

// <<< MUDANÇA: Quando a página carregar, chama a função da API
$(document).ready(function() {
    carregarDenuncias();
});