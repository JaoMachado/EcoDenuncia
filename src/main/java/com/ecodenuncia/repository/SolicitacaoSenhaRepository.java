package com.ecodenuncia.repository;

import com.ecodenuncia.model.Solicitacao_senhas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SolicitacaoSenhaRepository extends JpaRepository<Solicitacao_senhas, Long> {
    boolean existsByToken(String token);
    Optional<Solicitacao_senhas> findByToken(String token); 
}

