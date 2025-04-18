package com.schedulify.backend.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthProviderEnum {
    LOCAL("LOCAL"),
    GOOGLE("GOOGLE"),
    FACEBOOK("FACEBOOK");

    private final String provider;
}