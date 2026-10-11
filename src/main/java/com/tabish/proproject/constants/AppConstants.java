package com.tabish.proproject.constants;

public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    // Pagination defaults
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    // Date/Time patterns
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    // Success Messages
    public static final String USER_REGISTERED_SUCCESS = "User registered successfully.";
    public static final String USER_LOGGED_IN_SUCCESS = "User authenticated successfully.";
    public static final String USER_FETCHED_SUCCESS = "User retrieved successfully.";
    public static final String USERS_FETCHED_SUCCESS = "Users retrieved successfully.";
    public static final String USER_UPDATED_SUCCESS = "User updated successfully.";
    public static final String USER_DELETED_SUCCESS = "User deleted successfully.";
    public static final String PASSWORD_CHANGED_SUCCESS = "Password changed successfully.";
    public static final String TOKEN_REFRESHED_SUCCESS = "Token refreshed successfully.";
    public static final String LOGOUT_SUCCESS = "Logged out successfully.";
    public static final String AI_RESPONSE_SUCCESS = "AI response generated successfully.";
}

