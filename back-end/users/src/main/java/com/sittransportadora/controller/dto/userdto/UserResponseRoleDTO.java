package com.sittransportadora.controller.dto.userdto;

import com.sittransportadora.model.User;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponseRoleDTO(UUID uuid, String name, String email, Set<String> roles) {

    public static UserResponseRoleDTO fromEntity(User user) {
        Set<String> roleNames = user.getRoles()
                .stream()
                .map(role -> role.getName()) // Pega os nomes das roles (ex: "ROLE_ADMIN")
                .collect(Collectors.toSet());

        return new UserResponseRoleDTO(user.getUuid(), user.getName(), user.getEmail(), roleNames);
    }
}
