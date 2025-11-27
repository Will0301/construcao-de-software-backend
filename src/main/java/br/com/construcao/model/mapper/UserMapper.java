package br.com.construcao.model.mapper;

import br.com.construcao.model.dto.request.UserRequest;
import br.com.construcao.model.dto.response.UserResponse;
import br.com.construcao.infrastructure.entity.UserEntity;

public class UserMapper {

    public static UserResponse toDTO(UserEntity entity) {
        if (entity == null) return null;
        return UserResponse.builder()
                .name(entity.getName())
                .email(entity.getEmail())
                .build();
    }

    public static UserEntity toEntity(UserResponse dto) {
        if (dto == null) return null;
        return UserEntity.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserEntity toEntity(UserRequest dto) {
        if (dto == null) return null;
        return UserEntity.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }
}
