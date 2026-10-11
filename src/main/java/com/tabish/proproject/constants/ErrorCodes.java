package com.tabish.proproject.constants;

public final class ErrorCodes {

    private ErrorCodes() {
        // Prevent instantiation
    }

    public static final String VALIDATION_FAILED = "ERR_VALIDATION_001";
    public static final String USER_NOT_FOUND = "ERR_USER_001";
    public static final String USER_ALREADY_EXISTS = "ERR_USER_002";
    public static final String INVALID_CREDENTIALS = "ERR_AUTH_001";
    public static final String UNAUTHORIZED = "ERR_AUTH_002";
    public static final String ACCESS_DENIED = "ERR_AUTH_003";
    public static final String TOKEN_EXPIRED = "ERR_AUTH_004";
    public static final String TOKEN_INVALID = "ERR_AUTH_005";
    public static final String REFRESH_TOKEN_NOT_FOUND = "ERR_AUTH_006";
    public static final String REFRESH_TOKEN_EXPIRED = "ERR_AUTH_007";
    public static final String RESOURCE_NOT_FOUND = "ERR_RESOURCE_001";
    public static final String BAD_REQUEST = "ERR_REQ_001";
    public static final String INTERNAL_SERVER_ERROR = "ERR_SRV_001";
    public static final String AI_SERVICE_ERROR = "ERR_AI_001";
}

