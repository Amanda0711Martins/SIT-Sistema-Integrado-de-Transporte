package com.sittransportadora.controller.dto.userdto;

public record AuthStatusResponse(boolean isAuthenticated, String token) {
}
