package com.ecodenuncia.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Diz ao Spring que esta classe vai responder requisições web
public class HealthCheckController {

    @GetMapping("/denun") // Define a URL: http://localhost:8080/eco
    public String healthCheck() {
        return "SERVIDOR ATT! 🚀";
    }
}