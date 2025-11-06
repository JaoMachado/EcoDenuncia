package com.ecodenuncia.controller;

import com.ecodenuncia.model.Solicitacao_senhas;
import com.ecodenuncia.repository.SolicitacaoSenhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class CadastrarNovaSenhaController {

    @Autowired
    private SolicitacaoSenhaRepository solicitacaoSenhaRepository;

    @GetMapping("/validar_token")
    public RedirectView validarToken(@RequestParam("token") String token) {
        System.out.println("🔍 Verificando token: " + token);

        Optional<Solicitacao_senhas> solicitacaoOpt = solicitacaoSenhaRepository.findByToken(token);

        if (solicitacaoOpt.isEmpty()) {
            System.out.println("❌ Token inválido");
            return new RedirectView("/erro_token.html?mensagem=Link inválido");
        }

        Solicitacao_senhas solicitacao = solicitacaoOpt.get();

        if (!"PENDENTE".equalsIgnoreCase(solicitacao.getStatus().name())) {
            System.out.println("❌ Token já usado ou inválido");
            return new RedirectView("/erro_token.html?mensagem=Token já usado");
        }

        if (solicitacao.getDataExpiracao().isBefore(LocalDateTime.now())) {
            System.out.println("❌ Token expirado");
            return new RedirectView("/erro_token.html?mensagem=Link expirado");
        }
        System.out.println("Retornou token correto");
        // ✅ Token válido — redireciona para o HTML estático com token como query param
        return new RedirectView("/cadastrar_nova_senha.html?token=" + token);
    }
    

}
