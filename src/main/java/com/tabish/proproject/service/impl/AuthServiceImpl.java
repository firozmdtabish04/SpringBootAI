package com.tabish.proproject.service.impl;

import com.tabish.proproject.dto.request.LoginRequest;
import com.tabish.proproject.dto.request.RefreshTokenRequest;
import com.tabish.proproject.dto.request.RegisterRequest;
import com.tabish.proproject.dto.response.AuthResponse;
import com.tabish.proproject.dto.response.TokenRefreshResponse;
import com.tabish.proproject.dto.response.UserResponse;
import com.tabish.proproject.entity.RefreshToken;
import com.tabish.proproject.entity.Role;
import com.tabish.proproject.entity.User;
import com.tabish.proproject.exception.custom.InvalidCredentialsException;
import com.tabish.proproject.exception.custom.TokenRefreshException;
import com.tabish.proproject.exception.custom.UserAlreadyExistsException;
import com.tabish.proproject.exception.custom.UserNotFoundException;
import com.tabish.proproject.mapper.UserMapper;
import com.tabish.proproject.repository.RefreshTokenRepository;
import com.tabish.proproject.repository.UserRepository;
import com.tabish.proproject.security.CustomUserDetails;
import com.tabish.proproject.security.JwtTokenProvider;
import com.tabish.proproject.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;

    @Value("${app.jwt.refresh-expiration-ms:604800000}") // 7 days default
    private long refreshExpirationMs;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("username", registerRequest.getUsername());
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("email", registerRequest.getEmail());
        }

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        user.setProvider("LOCAL");

        User savedUser = userRepository.save(user);
        log.info("New user registered successfully with username: {}", savedUser.getUsername());

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            String accessToken = tokenProvider.generateToken(userDetails);
            RefreshToken refreshToken = createOrUpdateRefreshToken(user);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .expiresIn(tokenProvider.getExpirationTime())
                    .user(userMapper.toUserResponse(user))
                    .build();

        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid username/email or password");
        }
    }

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();

        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    CustomUserDetails userDetails = new CustomUserDetails(user);
                    String token = tokenProvider.generateToken(userDetails);
                    return TokenRefreshResponse.builder()
                            .accessToken(token)
                            .refreshToken(requestRefreshToken)
                            .expiresIn(tokenProvider.getExpirationTime())
                            .build();
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token was not found in database"));
    }

    @Override
    @Transactional
    public void logout(String username) {
        userRepository.findByUsername(username).ifPresent(refreshTokenRepository::deleteByUser);
        log.info("User {} logged out and refresh tokens cleared", username);
    }

    private RefreshToken createOrUpdateRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseGet(() -> RefreshToken.builder().user(user).build());

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0 || token.isRevoked()) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token has expired or been revoked. Please sign in again.");
        }
        return token;
    }
}

