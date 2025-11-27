package br.com.construcao.infrastructure.repository;

import br.com.construcao.infrastructure.entity.HolidayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface HolidayRepository extends JpaRepository<HolidayEntity, Long> {
    boolean existsByDate(LocalDate date);
}