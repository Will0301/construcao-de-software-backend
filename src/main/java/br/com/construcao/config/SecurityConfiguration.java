package br.com.construcao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    @Profile("!dev")
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**",
                                "/api/v1/users/healthz"
                        ).permitAll()
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "PROFESSIONAL")
                        .requestMatchers("/api/v1/appointments/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(this::jwtAuthenticationConverter))
                );

        return http.build();
    }

    private AbstractAuthenticationToken jwtAuthenticationConverter(Jwt jwt) {
        JwtGrantedAuthoritiesConverter delegate = new JwtGrantedAuthoritiesConverter();
        delegate.setAuthorityPrefix("ROLE_");
        delegate.setAuthoritiesClaimName("cognito:groups");

        Collection<GrantedAuthority> authorities = delegate.convert(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }


    // PARA RODAR LOCAL SEM COGNITO
    @Bean
    @Profile("dev")
    public SecurityFilterChain localSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/users/healthz",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new LocalFakeJwtFilter(), BearerTokenAuthenticationFilter.class);

        return http.build();
    }
}
