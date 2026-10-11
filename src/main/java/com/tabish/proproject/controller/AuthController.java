package com.tabish.proproject.controller;

import com.tabish.proproject.constants.AppConstants;
import com.tabish.proproject.dto.request.LoginRequest;
import com.tabish.proproject.dto.request.RefreshTokenRequest;
import com.tabish.proproject.dto.request.RegisterRequest;
import com.tabish.proproject.dto.response.ApiResponse;
import com.tabish.proproject.dto.response.AuthResponse;
import com.tabish.proproject.dto.response.TokenRefreshResponse;
import com.tabish.proproject.dto.response.UserResponse;
import com.tabish.proproject.service.AuthService;
import com.tabish.proproject.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token operations")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse response = authService.register(registerRequest);
        return new ResponseEntity<>(
                ApiResponse.created(AppConstants.USER_REGISTERED_SUCCESS, response),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials and receive JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.USER_LOGGED_IN_SUCCESS, response));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Generate a new access token using a valid refresh token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        TokenRefreshResponse response = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.TOKEN_REFRESHED_SUCCESS, response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout the current user and invalidate refresh tokens")
    public ResponseEntity<ApiResponse<Void>> logout() {
        SecurityUtils.getCurrentUsername().ifPresent(authService::logout);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.LOGOUT_SUCCESS));
    }
}

