package br.com.construcao.service;

import br.com.construcao.infrastructure.entity.AppointmentEntity;
import br.com.construcao.infrastructure.entity.ProviderEntity;
import br.com.construcao.infrastructure.entity.UserEntity;
import br.com.construcao.infrastructure.repository.*;
import br.com.construcao.model.dto.request.AppointmentRequest;
import br.com.construcao.model.dto.response.AppointmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final BlockRepository blockRepo;
    private final HolidayRepository holidayRepo;
    private final ProviderRepository providerRepo;
    private final UserRepository userRepo;

    public AppointmentResponse create(Long clientId, AppointmentRequest request) {

        //Validação: É feriado?
        if (holidayRepo.existsByDate(request.start().toLocalDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível agendar em feriados.");
        }

        //Validação: O médico bloqueou a agenda?
        if (blockRepo.existsConflict(request.providerId(), request.start(), request.end())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O prestador possui um bloqueio neste horário.");
        }

        //Validação: Já tem outro cliente marcado?
        if (appointmentRepo.existsConflict(request.providerId(), request.start(), request.end())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Horário indisponível.");
        }

        //Buscar os envolvidos no banco
        ProviderEntity provider = providerRepo.findById(request.providerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prestador não encontrado"));

        UserEntity client = userRepo.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        //Salvar o Agendamento (Status CREATED
        AppointmentEntity entity = AppointmentEntity.builder()
                .provider(provider)
                .client(client)
                .startTime(request.start())
                .endTime(request.end())
                .status("CREATED")
                .build();

        AppointmentEntity saved = appointmentRepo.save(entity);

        //Converter para DTO de resposta
        return new AppointmentResponse(
                saved.getId(),
                provider.getUser().getName(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getStatus()
        );
    }

    public void cancelAppointment(Long appointmentId, Long userId) {
        AppointmentEntity appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento não encontrado"));

        //Validação de Ownership (Só quem criou ou o admin pode cancelar)
        if (!appointment.getClient().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para cancelar este agendamento");
        }

        //Regra de Negócio: Cancelar apenas se permitido (ex: não pode cancelar agendamento passado)
        if (appointment.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível cancelar agendamentos passados.");
        }

        appointment.setStatus("CANCELED");
        appointmentRepo.save(appointment);
    }
}
