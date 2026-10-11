package com.tabish.proproject.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tabish.proproject.dto.response.ApiResponse;
import com.tabish.proproject.dto.response.AuthResponse;
import com.tabish.proproject.dto.response.UserResponse;
import com.tabish.proproject.entity.User;
import com.tabish.proproject.mapper.UserMapper;
import com.tabish.proproject.repository.UserRepository;
import com.tabish.proproject.security.CustomUserDetails;
import com.tabish.proproject.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email).orElseThrow();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        String token = tokenProvider.generateToken(userDetails);
        UserResponse userResponse = userMapper.toUserResponse(user);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(token)
                .expiresIn(tokenProvider.getExpirationTime())
                .user(userResponse)
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), ApiResponse.success("OAuth2 authentication successful", authResponse));
    }
}

