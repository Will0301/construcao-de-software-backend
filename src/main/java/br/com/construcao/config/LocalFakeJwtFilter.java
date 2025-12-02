package br.com.construcao.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


// PARA RODAR LOCAL SEM COGNITO

public class LocalFakeJwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Se o header não existir, segue sem autenticação
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Captura o "token fake"
        String token = auth.substring(7);

        // Exemplo: token = "admin" -> usuario admin
        String email = "dev@example.com";
        List<GrantedAuthority> roles = List.of();

        switch (token) {
            case "admin":
                roles = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
                break;
            case "prof":
                roles = List.of(new SimpleGrantedAuthority("ROLE_PROFESSIONAL"));
                break;
            case "client":
                roles = List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));
                break;
            default:
                roles = List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, roles);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
