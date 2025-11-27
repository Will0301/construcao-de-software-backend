package br.com.construcao.controller;

import br.com.construcao.infrastructure.entity.UserEntity;
import br.com.construcao.infrastructure.repository.UserRepository;
import br.com.construcao.model.dto.response.AppointmentResponse;
import br.com.construcao.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/appointments")
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    @GetMapping("/mine")
    public ResponseEntity<List<AppointmentResponse>> listMyAppointmentsAsProvider(JwtAuthenticationToken token) {

        String email = token.getTokenAttributes().get("email").toString();

        UserEntity currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado no Cognito, mas não encontrado no banco de dados local."));

        List<AppointmentResponse> list = appointmentService.listByProvider(currentUser.getId());
        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> listAll() {
        throw new UnsupportedOperationException("Implementar se quiser 'ver tudo'");
    }
}

