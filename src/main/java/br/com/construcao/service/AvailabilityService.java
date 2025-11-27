package br.com.construcao.service;

import br.com.construcao.infrastructure.entity.AppointmentEntity;
import br.com.construcao.infrastructure.entity.BlockEntity;
import br.com.construcao.infrastructure.entity.ScheduleRuleEntity;
import br.com.construcao.infrastructure.repository.*;
import br.com.construcao.model.dto.response.AvailabilityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final ScheduleRuleRepository scheduleRuleRepo;
    private final AppointmentRepository appointmentRepo;
    private final BlockRepository blockRepo;
    private final HolidayRepository holidayRepo;
    private final ProviderRepository providerRepo;

    public AvailabilityResponse getAvailability(Long providerId, LocalDate date) {
        //Verificar Feriado
        if (holidayRepo.existsByDate(date)) {
            return new AvailabilityResponse(providerId, date, List.of()); // Lista vazia
        }

        // Buscar regra do dia (Java DayOfWeek: 1=Segunda ... 7=Domingo
        // Aqui assumindo que no banco você salvou 1=Segunda, etc.
        int dayOfWeek = date.getDayOfWeek().getValue();

        var ruleOptional = scheduleRuleRepo.findByProviderIdAndDayOfWeek(providerId, dayOfWeek);
        if (ruleOptional.isEmpty()) {
            return new AvailabilityResponse(providerId, date, List.of()); // Não trabalha nesse dia
        }
        ScheduleRuleEntity rule = ruleOptional.get();

        //Buscar Prestador para saber a duração do slot (30min? 60min?)
        var provider = providerRepo.findById(providerId).orElseThrow();
        int slotMinutes = provider.getSlotDurationMinutes() != null ? provider.getSlotDurationMinutes() : 60;

        //Buscar Ocupações (Appointments e Blocks) do dia
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<AppointmentEntity> appointments = appointmentRepo.findByProviderAndDate(providerId, startOfDay, endOfDay);
        List<BlockEntity> blocks = blockRepo.findByProviderAndDate(providerId, startOfDay, endOfDay);

        //Algoritmo de Geração de Slots
        List<String> availableSlots = new ArrayList<>();
        LocalTime currentTime = rule.getStartTime();

        while (currentTime.isBefore(rule.getEndTime())) {
            LocalTime slotEnd = currentTime.plusMinutes(slotMinutes);

            //Se o slot passar do horário de saída, para.
            if (slotEnd.isAfter(rule.getEndTime())) break;

            //Monta o range do slot atual para comparação
            LocalDateTime slotStartDateTime = date.atTime(currentTime);
            LocalDateTime slotEndDateTime = date.atTime(slotEnd);

            //Verifica conflitos
            boolean isFree = isSlotFree(slotStartDateTime, slotEndDateTime, appointments, blocks, rule);

            if (isFree) {
                availableSlots.add(currentTime.toString());
            }

            //Próximo slot
            currentTime = slotEnd;
        }

        return new AvailabilityResponse(providerId, date, availableSlots);
    }

    private boolean isSlotFree(LocalDateTime start, LocalDateTime end,
                               List<AppointmentEntity> apps, List<BlockEntity> blocks,
                               ScheduleRuleEntity rule) {

        LocalTime timeStart = start.toLocalTime();

        //Verifica Pausa/Almoço (definido na Rule)
        if (rule.getBreakStart() != null && rule.getBreakEnd() != null) {
            //Se o slot começa dentro do almoço OU termina dentro do almoço
            boolean colideAlmoco = !timeStart.isBefore(rule.getBreakStart()) && timeStart.isBefore(rule.getBreakEnd());
            if (colideAlmoco) return false;
        }

        //Verifica Agendamentos
        for (AppointmentEntity app : apps) {
            //Lógica de interseção: (StartA < EndB) e (EndA > StartB)
            if (app.getStartTime().isBefore(end) && app.getEndTime().isAfter(start)) {
                return false;
            }
        }

        //Verifica Bloqueios
        for (BlockEntity block : blocks) {
            if (block.getStartTime().isBefore(end) && block.getEndTime().isAfter(start)) {
                return false;
            }
        }

        return true;
    }
}
