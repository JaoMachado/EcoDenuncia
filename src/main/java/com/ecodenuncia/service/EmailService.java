package com.ecodenuncia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void mandar_email(String email, String token) {
        System.out.println("enviando para email: " + email + "e token: " + token);
        String link = "http://localhost:8080/validar_token?token=" + token;
        String assunto = "Recuperação de Senha - EcoDenúncia";
        String mensagem = "Olá! Para redefinir sua senha, acesse o link abaixo:\n\n" + link +
                          "\n\nO link expira em 1 hora.";

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject(assunto);
        mail.setText(mensagem);
        mail.setFrom("leo.solovijovas@gmail.com");

        mailSender.send(mail);

        System.out.println("📨 E-mail enviado com sucesso para: " + email);
    }
}
