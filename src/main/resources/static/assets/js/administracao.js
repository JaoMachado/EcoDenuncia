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
        // Se o array (que veio do banco) estiver vazio
        mensagem?.classList.remove('d-none');
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">Nenhum cadastro pendente.</td></tr>';
        return;
    } 
    
    // Esconde a mensagem se tiver dados
    mensagem?.classList.add('d-none');
    
    // Cria as linhas da tabela
    usuariosPendentes.forEach(user => {
        const tr = document.createElement('tr');
        // Usamos os nomes do DTO (id, nome, email, etc.)
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
window.aprovar = async function(id) {
    if (!confirm('Tem certeza que deseja aprovar este inspetor?')) {
        return;
    }
    
    try {
        // Chama o endpoint: POST /api/admin/aprovar/{id}
        const response = await fetch(`/api/admin/aprovar/${id}`, {
            method: 'POST'
        });

        if (response.ok) {
            alert('Inspetor aprovado com sucesso!');
            carregarPendentes(); // Recarrega a lista do banco
        } else {
            alert('Erro ao aprovar o inspetor.');
        }
    } catch (error) {
        console.error('Erro na requisição:', error);
        alert('Erro de conexão ao tentar aprovar.');
    }
};

/**
 * NOVA FUNÇÃO: Chama a API para REJEITAR (excluir) um usuário
 * @param {number} id - O ID do usuário (vem do DTO)
 */
window.rejeitar = async function(id) {
    if (!confirm('Tem certeza que deseja REJEITAR este cadastro? Esta ação não pode ser desfeita.')) {
        return;
    }
    
    try {
        // Chama o endpoint: POST /api/admin/rejeitar/{id}
        const response = await fetch(`/api/admin/rejeitar/${id}`, {
            method: 'POST'
        });

        if (response.ok) {
            alert('Cadastro rejeitado e excluído com sucesso.');
            carregarPendentes(); // Recarrega a lista do banco
        } else {
            alert('Erro ao rejeitar o cadastro.');
        }
    } catch (error) {
        console.error('Erro na requisição:', error);
        alert('Erro de conexão ao tentar rejeitar.');
    }
};

/**
 * NOVA FUNÇÃO: Carrega os dados da API
 * (substitui o array de simulação)
 */
async function carregarPendentes() {
    try {
        // Chama o endpoint: GET /api/admin/pendentes
        const response = await fetch('/api/admin/pendentes');
        
        if (!response.ok) {
            // Se der erro 403 (Proibido), é o Spring Security bloqueando
            if(response.status === 403) {
                 alert('Acesso negado! Você não tem permissão de Administrador.');
            }
            throw new Error('Falha ao buscar dados do servidor.');
        }
        
        // Guarda os dados do banco no nosso array global
        usuariosPendentes = await response.json(); 
        
        // Renderiza a tabela com os dados REAIS
        renderTabela(); 
        
    } catch (error) {
        console.error('Erro ao carregar usuários pendentes:', error);
        alert('Não foi possível carregar os dados do servidor.');
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