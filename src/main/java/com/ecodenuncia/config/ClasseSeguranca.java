package com.ecodenuncia.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Nova Importação
import org.springframework.security.authentication.AuthenticationManager; // Nova Importação
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Nova Importação
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // Nova Importação
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Nova Importação

@Configuration
@EnableWebSecurity
public class ClasseSeguranca {

    @Autowired
    private SegurancaFiltro securityFilter; // Nosso "Porteiro" JWT

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desabilita o CSRF (padrão para APIs JWT)
                .csrf(csrf -> csrf.disable())
                
                // 2. Torna a segurança STATELESS (sem Sessão/Cookie)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                .authorizeHttpRequests(authorize -> authorize
                        
                        // 3. Libera o acesso aos endpoints PÚBLICOS (Visitante)
                        .requestMatchers(HttpMethod.POST, "/api/cadastro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                        
                        // 4. Libera o acesso aos ARQUIVOS ESTÁTICOS (HTML, CSS, JS...)
                        .requestMatchers("/", "/index.html", "/login.html", "/cadastro.html",
                                "/boas_praticas.html", "/sobre.html", "/recuperar_senha.html",
                                "/assets/**").permitAll()
                        
                        // 5. Libera os utilitários de dev (H2 e Health Check)
                        .requestMatchers("/denun", "/h2-console/**").permitAll()
                        
                        // 6. REGRAS DE AUTORIZAÇÃO (Baseado nos seus novos Controllers)
                        
                        // Rotas de Admin (Caso 3.24, 3.23)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        
                        // Rotas de Gestão (Caso 3.20, 3.21, 3.22)
                        .requestMatchers("/api/denuncias/**").hasAnyRole("INSPETOR", "ADMIN")
                        
                        // (Adicionar aqui as rotas do Usuário Logado, ex: /api/denunciar)
                        
                        // 7. Todas as OUTRAS rotas exigem autenticação
                        .anyRequest().authenticated()
                )
                
                // 8. Adiciona nosso "Porteiro" (Filtro JWT) para rodar ANTES
                //    do filtro padrão de login do Spring
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        // Permite que o H2 Console seja exibido em um frame
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    // Bean do Criptografador de Senha (Já estava OK)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean do Gerenciador de Autenticação (NOVO E CRUCIAL)
    // Precisamos disso para o nosso Controller de Login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}