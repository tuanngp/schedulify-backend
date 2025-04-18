package com.schedulify.backend.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

    // API Versioning
    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api/" + API_VERSION;

    // Default Pagination
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    // Authentication
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 24 * 60 * 60; // 24 hours
    public static final long REFRESH_TOKEN_VALIDITY_SECONDS = 7 * 24 * 60 * 60; // 7 days

    // User Roles
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_CREATOR = "ROLE_CREATOR";

    // Auth Providers
    public static final String AUTH_PROVIDER_LOCAL = "LOCAL";
    public static final String AUTH_PROVIDER_GOOGLE = "GOOGLE";
    public static final String AUTH_PROVIDER_FACEBOOK = "FACEBOOK";

    // File Upload
    public static final int MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif"};

    // Cache TTL
    public static final long CACHE_TTL_FIVE_MINUTES = 5 * 60; // 5 minutes
    public static final long CACHE_TTL_ONE_HOUR = 60 * 60; // 1 hour
    public static final long CACHE_TTL_ONE_DAY = 24 * 60 * 60; // 1 day
}