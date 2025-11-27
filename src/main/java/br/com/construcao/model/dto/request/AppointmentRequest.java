package br.com.construcao.model.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;


import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull(message = "O ID do prestador é obrigatório")
        Long providerId,

        @NotNull(message = "O início é obrigatório")
        @Future(message = "A data deve ser futura") // Validação automática do Spring
        LocalDateTime start,

        @NotNull(message = "O fim é obrigatório")
        @Future(message = "A data deve ser futura")
        LocalDateTime end
) {}
