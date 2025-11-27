package br.com.construcao.infrastructure.repository;

import br.com.construcao.infrastructure.entity.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<BlockEntity, Long> {

    // Verifica se existe algum bloqueio batendo com o horário
    @Query("""
        SELECT COUNT(b) > 0 FROM BlockEntity b
        WHERE b.provider.id = :providerId
        AND (b.startTime < :endTime AND b.endTime > :startTime)
    """)
    boolean existsConflict(Long providerId, LocalDateTime startTime, LocalDateTime endTime);

    // NOVO: Lista todos os bloqueios do dia
    @Query("SELECT b FROM BlockEntity b WHERE b.provider.id = :providerId " +
            "AND b.startTime >= :startOfDay AND b.endTime <= :endOfDay")
    List<BlockEntity> findByProviderAndDate(Long providerId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}