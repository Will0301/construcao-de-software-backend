package br.com.construcao.service;

import br.com.construcao.model.dto.request.UserRequest;
import br.com.construcao.model.dto.response.UserResponse;
import br.com.construcao.model.mapper.UserMapper;
import br.com.construcao.infrastructure.repository.UserRepository;
import br.com.construcao.infrastructure.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    public UserResponse addUser(UserRequest request) {
        UserEntity entity = UserMapper.toEntity(request);
        UserEntity saved = userRepository.save(entity);
        return UserMapper.toDTO(saved);
    }

    public void delete(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("Usuário não encontrado com id: " + email);
        }
        userRepository.deleteByEmail(email);
    }
}
