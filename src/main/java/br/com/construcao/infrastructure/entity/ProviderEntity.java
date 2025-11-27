package br.com.construcao.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "providers")
@Data // Faz Getters, Setters, toString, etc.
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento 1-pra-1: Um Usuário pode ser um Prestador
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    // Configuração: Quanto tempo dura cada atendimento? (ex: 60 min)
    @Column(name = "slot_duration_minutes")
    private Integer slotDurationMinutes;
    
    private Boolean active;
}