// Este array vai guardar os usuários que vêm do banco (começa vazio)
let usuariosPendentes = [];

/**
 * Função que renderiza a tabela (baseada nos dados da API)
 */
function renderTabela() {
    const tbody = document.querySelector('#tabelaInspetores tbody'); 
    tbody.innerHTML = ''; // Limpa a tabela

    const mensagem = document.getElementById('mensagemSemInspetor');

    if (usuariosPendentes.length === 0) {
        mensagem?.classList.remove('d-none');
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">Nenhum cadastro pendente.</td></tr>';
        return;
    } 
    
    mensagem?.classList.add('d-none');
    
    usuariosPendentes.forEach(user => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${user.nome}</td>
            <td>${user.email}</td>
            <td>${user.mensagemCapacitacao || '-'}</td>
            <td>${user.status}</td>
            <td>
                <button class="btn btn-sm btn-success" onclick="aprovar(${user.id})">Aprovar</button>
                <button class="btn btn-sm btn-danger" onclick="rejeitar(${user.id})">Rejeitar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

/**
 * NOVA FUNÇÃO: Chama a API para APROVAR um usuário
 * @param {number} id - O ID do usuário (vem do DTO)
 */
window.aprovar = function(id) {
    // <<< MUDANÇA: Substituído o 'confirm()' por 'swal()'
    swal({
        title: "Aprovar Inspetor?",
        text: "Tem certeza que deseja aprovar este cadastro?",
        icon: "info",
        buttons: {
            cancel: "Cancelar", // Texto do botão de cancelar
            confirm: "Aprovar"  // Texto do botão de confirmar
        },
    }).then(async (vaiAprovar) => {
        // O 'try/catch' agora vai DENTRO do '.then()'
        if (vaiAprovar) {
            try {
                const response = await fetch(`/api/admin/aprovar/${id}`, {
                    method: 'POST'
                });

                if (response.ok) {
                    // <<< MUDANÇA: Substituído o 'alert()' por 'swal()'
                    swal("Aprovado!", "Inspetor aprovado com sucesso.", "success");
                    carregarPendentes(); // Recarrega a lista
                } else {
                    // <<< MUDANÇA: Substituído o 'alert()' por 'swal()'
                    swal("Erro", "Erro ao aprovar o inspetor. Verifique o console.", "error");
                }
            } catch (error) {
                console.error('Erro na requisição:', error);
                // <<< MUDANÇA: Substituído o 'alert()' por 'swal()'
                swal("Erro de Conexão", "Não foi possível conectar ao servidor.", "error");
            }
        }
    });
};

/**
 * NOVA FUNÇÃO: Chama a API para REJEITAR (excluir) um usuário
 * @param {number} id - O ID do usuário (vem do DTO)
 */
window.rejeitar = function(id) {
    // <<< MUDANÇA: Substituído o 'confirm()' por 'swal()' (com modo perigoso)
    swal({
        title: "Tem certeza?",
        text: "Uma vez rejeitado, o cadastro será excluído! Esta ação não pode ser desfeita.",
        icon: "warning",
        buttons: {
            cancel: "Cancelar",
            confirm: "Rejeitar"
        },
        dangerMode: true, // Deixa o botão de confirmação vermelho
    }).then(async (vaiRejeitar) => {
        if (vaiRejeitar) {
            try {
                const response = await fetch(`/api/admin/rejeitar/${id}`, {
                    method: 'POST'
                });

                if (response.ok) {
                    // <<< MUDANÇA: Substituído o 'alert()' por 'swal()'
                    swal("Rejeitado!", "Cadastro rejeitado e excluído com sucesso.", "success");
                    carregarPendentes(); // Recarrega a lista
                } else {
                    // <<< MUDANÇA: Substituído o 'alert()' por 'swal()'
                    swal("Erro", "Erro ao rejeitar o cadastro.", "error");
                }
            } catch (error) {
                console.error('Erro na requisição:', error);
                // <<< MUDANÇA: Substituído o 'alert()' por 'swal()'
                swal("Erro de Conexão", "Não foi possível conectar ao servidor.", "error");
            }
        }
    });
};

/**
 * NOVA FUNÇÃO: Carrega os dados da API
 * (substitui o array de simulação)
 */
async function carregarPendentes() {
    try {
        const response = await fetch('/api/admin/pendentes');
        
        if (!response.ok) {
            // Se der erro 403 (Proibido), é o Spring Security bloqueando
            if(response.status === 403) {
                 // <<< MUDANÇA: Substituído o 'alert()' por 'swal()'
                 swal("Acesso Negado", "Você não tem permissão de Administrador para ver esta página.", "error");
            }
            throw new Error('Falha ao buscar dados do servidor.');
        }
        
        usuariosPendentes = await response.json(); 
        renderTabela(); 
        
    } catch (error) {
        console.error('Erro ao carregar usuários pendentes:', error);
        // <<< MUDANÇA: Substituído o 'alert()' por 'swal()'
        // Evita mostrar o alerta de permissão duas vezes
        if (error.message.includes('403')) {
             // O swal de permissão já foi mostrado
        } else {
             swal("Erro ao Carregar", "Não foi possível carregar os dados do servidor.", "error");
        }
    }
}

/**
 * INICIALIZAÇÃO:
 * Quando a página (documento) estiver pronta,
 * chama a função para carregar os dados da API.
 */
$(document).ready(function() {
    carregarPendentes();
});