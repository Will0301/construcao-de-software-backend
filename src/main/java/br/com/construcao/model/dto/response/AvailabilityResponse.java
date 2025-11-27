package br.com.construcao.model.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AvailabilityResponse(
        Long providerId,
        LocalDate date,
        List<String> availableTimes // Ex: ["09:00", "09:30", "15:00"]
) {}
