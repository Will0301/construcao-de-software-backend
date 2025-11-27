package br.com.construcao.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quem vai atender
    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderEntity provider;

    // Quem será atendido (Cliente)
    @ManyToOne
    @JoinColumn(name = "client_id")
    private UserEntity client;

    // Horários
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    // Status: CREATED, CONFIRMED, CANCELED
    private String status;

    private String notes;
}
