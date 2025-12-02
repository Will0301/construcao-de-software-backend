package br.com.construcao.service;

import br.com.construcao.infrastructure.entity.ProviderEntity;
import br.com.construcao.infrastructure.entity.ScheduleRuleEntity;
import br.com.construcao.infrastructure.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    ScheduleRuleRepository scheduleRuleRepo;
    @Mock
    AppointmentRepository appointmentRepo;
    @Mock
    BlockRepository blockRepo;
    @Mock
    HolidayRepository holidayRepo;
    @Mock
    ProviderRepository providerRepo;

    @InjectMocks
    AvailabilityService service;

    @Test
    void deveGerarHorariosLivres() {
        Long providerId = 1L;
        LocalDate data = LocalDate.of(2025, 12, 1); // Segunda-feira

        // Mock do Prestador (Slots de 60 min)
        ProviderEntity provider = ProviderEntity.builder().id(1L).slotDurationMinutes(60).build();
        when(providerRepo.findById(1L)).thenReturn(Optional.of(provider));

        // Mock da Regra (Trabalha das 08:00 as 12:00)
        ScheduleRuleEntity rule = ScheduleRuleEntity.builder()
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0)) // Deve gerar: 08:00, 09:00, 10:00, 11:00
                .build();
        when(scheduleRuleRepo.findByProviderIdAndDayOfWeek(1L, 1)).thenReturn(Optional.of(rule));

        when(holidayRepo.existsByDate(data)).thenReturn(false);
        when(appointmentRepo.findByProviderAndDate(any(), any(), any())).thenReturn(List.of());

        when(blockRepo.findByProviderAndDate(any(), any(), any())).thenReturn(List.of());

        var result = service.getAvailability(providerId, data);

        assertEquals(4, result.availableTimes().size());
        assertEquals("08:00", result.availableTimes().get(0));
        assertEquals("11:00", result.availableTimes().get(3));

        System.out.println("Slots Gerados: " + result.availableTimes());
    }
}
