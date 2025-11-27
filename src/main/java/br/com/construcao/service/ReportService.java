package br.com.construcao.service;

import br.com.construcao.infrastructure.entity.AppointmentEntity;
import br.com.construcao.infrastructure.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AppointmentRepository appointmentRepository;
    private final S3Client s3Client;

    @Value("${app.reports.bucket}")
    private String bucketName;

    public String generateWeeklyReportAndUpload() {
        LocalDate today = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        LocalDateTime start = monday.atStartOfDay();
        LocalDateTime end = sunday.atTime(23, 59);

        List<AppointmentEntity> list =
                appointmentRepository.findAppointmentsInWeek(start, end);

        String csv = buildCsv(list);

        String key = "reports/appointments-" + monday + "-to-" + sunday + ".csv";

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType("text/csv")
                        .build(),
                RequestBody.fromString(csv)
        );

        return key;
    }

    private String buildCsv(List<AppointmentEntity> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,providerId,clientId,startTime,endTime,status\n");
        for (AppointmentEntity a : list) {
            sb.append(a.getId()).append(',')
                    .append(a.getProvider().getId()).append(',')
                    .append(a.getClient().getId()).append(',')
                    .append(a.getStartTime()).append(',')
                    .append(a.getEndTime()).append(',')
                    .append(a.getStatus()).append('\n');
        }
        return sb.toString();
    }
}

