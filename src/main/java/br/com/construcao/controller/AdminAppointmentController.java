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
import org.springframework.data.domain.Page;


import java.util.List;

@RequestMapping("/api/v1/admin/appointments")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    @GetMapping("/list")
    public ResponseEntity<List<AppointmentResponse>> listMyAppointmentsAsProvider(JwtAuthenticationToken token) {
        String email = token.getTokenAttributes().get("email").toString();

        UserEntity currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado no Cognito, mas não encontrado no banco de dados local."));

        List<AppointmentResponse> list = appointmentService.listByProvider(currentUser.getId());
        return ResponseEntity.ok(list);
    }

    // NOVO: listagem paginada + filtro de status
    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status
    ) {
        Page<AppointmentResponse> result = appointmentService.listPaged(status, page, size);
        return ResponseEntity.ok(result);
    }
}


