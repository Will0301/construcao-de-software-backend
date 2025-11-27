package br.com.construcao.controller;


import br.com.construcao.infrastructure.entity.UserEntity;
import br.com.construcao.infrastructure.repository.UserRepository;
import br.com.construcao.model.dto.request.AppointmentRequest;
import br.com.construcao.model.dto.response.AppointmentResponse;
import br.com.construcao.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;
    private final UserRepository userRepository; // Necessário para converter o Email do Token em ID do Banco

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(
            @RequestBody @Valid AppointmentRequest request,
            JwtAuthenticationToken token // O Spring injeta o token validado aqui automaticamente
    ) {
        // 1. Extrair o email de dentro do Token JWT (Vindo do Cognito/Auth0)
        // O campo geralmente é "email", mas confirme se o seu provedor não manda como "username" ou "sub"
        String userEmail = token.getTokenAttributes().get("email").toString();

        // 2. Buscar o usuário correspondente no seu banco de dados local (PostgreSQL)
        UserEntity currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado no Cognito, mas não encontrado no banco de dados local."));

        // 3. Chamar o serviço passando o ID real do usuário do banco
        AppointmentResponse response = service.create(currentUser.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}