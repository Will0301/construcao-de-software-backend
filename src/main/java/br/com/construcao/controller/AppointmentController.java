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

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(
            @RequestBody @Valid AppointmentRequest request,
            JwtAuthenticationToken token
    ) {
        String userEmail = token.getTokenAttributes().get("email").toString();

        UserEntity currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado no Cognito, mas não encontrado no banco de dados local."));

        AppointmentResponse response = service.create(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<AppointmentResponse>> listMyAppointments(JwtAuthenticationToken token) {

        String userEmail = token.getTokenAttributes().get("email").toString();

        UserEntity currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado no Cognito, mas não encontrado no banco de dados local."));

        List<AppointmentResponse> appointments = service.listByClient(currentUser.getId());
        return ResponseEntity.ok(appointments);
    }
}
