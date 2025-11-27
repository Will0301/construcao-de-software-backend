package br.com.construcao.infrastructure.repository;

import br.com.construcao.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByName(String name);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);
}
