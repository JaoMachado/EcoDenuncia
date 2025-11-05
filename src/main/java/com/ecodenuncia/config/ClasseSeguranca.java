package com.ecodenuncia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class ClasseSeguranca {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita o CSRF (padrão para APIs, mas necessário aqui)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                // 1. Libera todos os seus arquivos HTML e a pasta "assets"
                //    Qualquer um (Visitante) pode ver essas páginas.
                .requestMatchers(
                        "/",
                        "/index.html",
                        "/login.html",
                        "/cadastro.html",
                        "/boas_praticas.html",
                        "/sobre.html",
                        "/recuperar_senha.html",
                        "/assets/**" // Libera tudo dentro da pasta assets (CSS, JS, Imagens)
                ).permitAll()
                // 2. Libera os ENDPOINTS da API para cadastro e login
                .requestMatchers("/api/cadastro", "/api/login").permitAll()
                // 3. Libera nossos endpoints de teste e banco
                .requestMatchers("/denun", "/h2-console/**").permitAll()
                // 4. Diz que QUALQUER OUTRA requisição precisa de login
                //    (Ex: /perfil.html, /denunciar.html, /api/denuncias)
                .anyRequest().authenticated()
                )
                // 5. Se alguém tentar acessar uma página protegida (ex: /perfil.html)
                //    sem estar logado, será redirecionado para /login.html
                .formLogin(form -> form.loginPage("/login.html").permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/index.html").permitAll());

        // Permite que o H2 Console seja exibido em um frame
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Usa o algoritmo BCrypt, que é o padrão de mercado
        return new BCryptPasswordEncoder();
    }
}
