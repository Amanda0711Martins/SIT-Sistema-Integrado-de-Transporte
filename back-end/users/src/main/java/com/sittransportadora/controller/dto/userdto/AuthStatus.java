package com.sittransportadora.controller.dto.userdto;

public record AuthStatus(boolean isAuthenticated, UserResponseRoleDTO user) {
}
