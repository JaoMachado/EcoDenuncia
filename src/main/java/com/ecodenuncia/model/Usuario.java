package com.ecodenuncia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity //Avisa que ao Spring q é uma entidade
@Table (name = "tb_usuarios") //define o nome da tabela como tb_usuarios para evitar ambiguidade
public class Usuario implements UserDetails{
    //imlementação do Userdetails para login
	
	@Id //define que é chave primaria
	@GeneratedValue(strategy = GenerationType.IDENTITY) //Define que a chave sera feita automaticamente pelo banco
	private Long id;
	
	// jpa define que não pode ser nulo
	//Validation: Usa a dependência "Validation" para garantir que não é vazio
	@NotBlank(message = "Nome/RazaoSocial é Obrigatoria")
	@Column (nullable = false)
	private String nomeRazaoSocial;
	
	// JPA Define que seja unico o cpf e não nulo
	@NotBlank(message = "Cnpj/Cpf Obrigatorio")
	@Column(nullable = false, unique = true)
	private String cpfCnpj;
	
	//Validation : Verificação do email
	@Email(message = "E-mail inválido")
    @NotBlank(message = "E-mail é obrigatório")
	//garante que é unico no banco.
	@Column(nullable = false, unique = true)
	private String email;
	
	
	//Declara um tamanho minima para a senha
	//E utilizando a classe validation verifica a senha.
	@Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
	@NotBlank(message = "Senha é obrigatória")
	//Não pode ser nula
	@Column(nullable = false)
	private String senha;
	
	@NotBlank(message = "Número de celular é obrigatório")
    @Column(nullable = false)
    private String celular;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;
    
    // Campos formulário de cadastro 
    private String tipoPessoa; // (Ex: "PESSOA_FISICA" ou "PESSOA_JURIDICA")
    private String tipoUsuario; // (Ex: "USUARIO", "INSPETOR")
    private String statusCadastro; // (Ex: "AGUARDANDO_APROVACAO", "APROVADO")
    private String mensagemCapacitacao;
	
    public Usuario() {
    	
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeRazaoSocial() {
		return nomeRazaoSocial;
	}

	public void setNomeRazaoSocial(String nomeRazaoSocial) {
		this.nomeRazaoSocial = nomeRazaoSocial;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public String getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(String tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public String getStatusCadastro() {
		return statusCadastro;
	}

	public void setStatusCadastro(String statusCadastro) {
		this.statusCadastro = statusCadastro;
	}

	public String getMensagemCapacitacao() {
		return mensagemCapacitacao;
	}

	public void setMensagemCapacitacao(String mensagemCapacitacao) {
		this.mensagemCapacitacao = mensagemCapacitacao;
	}
        
        //Classes abstratass chamada do UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Importe java.util.List e org.springframework.security.core.authority.SimpleGrantedAuthority
        
        // Define o "Papel" (Role) do usuário.
        // Ex: Se tipoUsuario for "INSPETOR", a Role será "ROLE_INSPETOR"
        // (O "ROLE_" é uma convenção obrigatória do Spring Security)
        
        // Adicione esta importação no topo do seu arquivo:
        // import java.util.List;
        // import org.springframework.security.core.authority.SimpleGrantedAuthority;
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.tipoUsuario));
    }

    @Override
    public String getPassword() {
        // Spring vai chamar este método para pegar a senha criptografada
        return this.senha;
    }

    @Override
    public String getUsername() {
        // O "username" no nosso sistema é o e-mail
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Por enquanto, a conta nunca expira
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Vamos considerar a conta "não bloqueada" se ela foi aprovada
        // Adicione esta importação no topo do seu arquivo:
        // import java.util.Objects;
        return Objects.equals(this.statusCadastro, "APROVADO");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // As credenciais nunca expiram
        return true;
    }

    @Override
    public boolean isEnabled() {
        // A conta está habilitada (mesma lógica do "não bloqueada")
        return Objects.equals(this.statusCadastro, "APROVADO");
    }
}
