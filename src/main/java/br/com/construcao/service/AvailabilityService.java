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
        if (holidayRepo.existsByDate(date)) {
            return new AvailabilityResponse(providerId, date, List.of()); // Lista vazia
        }

        int dayOfWeek = date.getDayOfWeek().getValue();

        var ruleOptional = scheduleRuleRepo.findByProviderIdAndDayOfWeek(providerId, dayOfWeek);
        if (ruleOptional.isEmpty()) {
            return new AvailabilityResponse(providerId, date, List.of()); // Não trabalha nesse dia
        }
        ScheduleRuleEntity rule = ruleOptional.get();

        var provider = providerRepo.findById(providerId).orElseThrow();
        int slotMinutes = provider.getSlotDurationMinutes() != null ? provider.getSlotDurationMinutes() : 60;

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<AppointmentEntity> appointments = appointmentRepo.findByProviderAndDate(providerId, startOfDay, endOfDay);
        List<BlockEntity> blocks = blockRepo.findByProviderAndDate(providerId, startOfDay, endOfDay);

        List<String> availableSlots = new ArrayList<>();
        LocalTime currentTime = rule.getStartTime();

        while (currentTime.isBefore(rule.getEndTime())) {
            LocalTime slotEnd = currentTime.plusMinutes(slotMinutes);

            if (slotEnd.isAfter(rule.getEndTime())) break;

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

        if (rule.getBreakStart() != null && rule.getBreakEnd() != null) {
            boolean colideAlmoco = !timeStart.isBefore(rule.getBreakStart()) && timeStart.isBefore(rule.getBreakEnd());
            if (colideAlmoco) return false;
        }

        for (AppointmentEntity app : apps) {
            if (app.getStartTime().isBefore(end) && app.getEndTime().isAfter(start)) {
                return false;
            }
        }

        for (BlockEntity block : blocks) {
            if (block.getStartTime().isBefore(end) && block.getEndTime().isAfter(start)) {
                return false;
            }
        }

        return true;
    }
}
