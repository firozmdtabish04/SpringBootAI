package com.tabish.proproject.service;

import com.tabish.proproject.dto.request.LoginRequest;
import com.tabish.proproject.dto.request.RefreshTokenRequest;
import com.tabish.proproject.dto.request.RegisterRequest;
import com.tabish.proproject.dto.response.AuthResponse;
import com.tabish.proproject.dto.response.TokenRefreshResponse;
import com.tabish.proproject.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);

    TokenRefreshResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    void logout(String username);
}

