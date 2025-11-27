package br.com.construcao.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "schedule_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderEntity provider;

    // 0 = Domingo, 1 = Segunda, ..., 6 = Sábado
    @Column(name = "day_of_week")
    private Integer dayOfWeek;

    private LocalTime startTime; // Ex: 09:00
    private LocalTime endTime;   // Ex: 18:00

    private LocalTime breakStart; // Almoço Início
    private LocalTime breakEnd;   // Almoço Fim
}
