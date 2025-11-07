package com.ecodenuncia.config;

import com.ecodenuncia.repository.UsuarioRepository;
import com.ecodenuncia.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Diz ao Spring que este é um componente
public class SegurancaFiltro extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Este método é o "Porteiro".
     * Ele é chamado em TODA requisição.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Tenta recuperar o token do cabeçalho
        String tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            // 2. Se o token veio, valida
            String subject = tokenService.getSubject(tokenJWT);
            
            if (subject != null) {
                // 3. Se o token é válido, busca o usuário no banco
                UserDetails usuario = usuarioRepository.findByEmail(subject)
                        .orElse(null);

                if (usuario != null) {
                    // 4. Se o usuário existe, informa ao Spring Security
                    // que ele está autenticado para esta requisição
                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        
        // 5. Continua a requisição (para o Controller ou para o próximo filtro)
        filterChain.doFilter(request, response);
    }

    /**
     * Método auxiliar para pegar o Token do Cabeçalho "Authorization"
     */
    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Remove o prefixo "Bearer "
            return authHeader.substring(7);
        }
        return null; // Não veio token
    }
}