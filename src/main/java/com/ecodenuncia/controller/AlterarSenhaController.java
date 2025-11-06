package com.ecodenuncia.controller;

import com.ecodenuncia.service.SenhaService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AlterarSenhaController {

    @Autowired
    private SenhaService senhaService;

    @PostMapping("/alterar_senha")
    public RedirectView alterarSenha(@RequestBody Map<String, String> dados) {
        String token = dados.get("token");
        String novaSenha = dados.get("novaSenha");

        try {
            senhaService.alterarSenha(token, novaSenha);
            return new RedirectView("/login.html?mensagem=Senha alterada com sucesso");
        } catch (RuntimeException e) {
            return new RedirectView("/erro_token.html?mensagem=" + e.getMessage());
        }
    }

}
