package com.schedulify.backend.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleEnum {
    USER("ROLE_USER", "Regular user"),
    ADMIN("ROLE_ADMIN", "Administrator"),
    CREATOR("ROLE_CREATOR", "Content Creator");

    private final String role;
    private final String description;
}