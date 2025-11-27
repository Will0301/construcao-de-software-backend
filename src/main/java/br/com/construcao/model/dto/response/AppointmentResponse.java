package br.com.construcao.model.dto.response;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        String providerName,
        LocalDateTime start,
        LocalDateTime end,
        String status
) {}
