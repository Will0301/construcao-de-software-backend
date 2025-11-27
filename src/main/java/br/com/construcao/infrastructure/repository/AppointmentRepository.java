package br.com.construcao.infrastructure.repository;

import br.com.construcao.infrastructure.entity.AppointmentEntity;
import br.com.construcao.infrastructure.entity.ProviderEntity;
import br.com.construcao.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    @Query("""
        SELECT COUNT(a) > 0 FROM AppointmentEntity a
        WHERE a.provider.id = :providerId
        AND a.status != 'CANCELED'
        AND (a.startTime < :endTime AND a.endTime > :startTime)
    """)
    boolean existsConflict(Long providerId, LocalDateTime startTime, LocalDateTime endTime);

    // Retorna a lista de agendamentos do dia para sabermos quais horários riscar da agenda
    @Query("""
        SELECT a FROM AppointmentEntity a
        WHERE a.provider.id = :providerId
        AND a.startTime >= :startOfDay
        AND a.endTime <= :endOfDay
        AND a.status != 'CANCELED'
    """)
    List<AppointmentEntity> findByProviderAndDate(Long providerId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<AppointmentEntity> findByClient(UserEntity client);

    List<AppointmentEntity> findByProvider(ProviderEntity provider);

    @Query("""
        SELECT a FROM AppointmentEntity a
        WHERE a.startTime BETWEEN :start AND :end
           OR a.endTime   BETWEEN :start AND :end
        """)
    List<AppointmentEntity> findAppointmentsInWeek(LocalDateTime start, LocalDateTime end);

}
