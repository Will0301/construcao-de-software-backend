package br.com.construcao.infrastructure.repository;

import br.com.construcao.infrastructure.entity.ScheduleRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRuleRepository extends JpaRepository<ScheduleRuleEntity, Long> {

    // "Me dê a regra de horário do Prestador X para o dia Y (ex: Segunda-feira)"
    Optional<ScheduleRuleEntity> findByProviderIdAndDayOfWeek(Long providerId, Integer dayOfWeek);
}
