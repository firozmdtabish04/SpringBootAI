package com.tabish.proproject.service.impl;

import com.tabish.proproject.dto.request.ChangePasswordRequest;
import com.tabish.proproject.dto.request.UpdateUserRequest;
import com.tabish.proproject.dto.response.UserResponse;
import com.tabish.proproject.entity.Role;
import com.tabish.proproject.entity.User;
import com.tabish.proproject.exception.custom.BadRequestException;
import com.tabish.proproject.exception.custom.UserAlreadyExistsException;
import com.tabish.proproject.exception.custom.UserNotFoundException;
import com.tabish.proproject.mapper.UserMapper;
import com.tabish.proproject.repository.UserRepository;
import com.tabish.proproject.service.UserService;
import com.tabish.proproject.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UserNotFoundException("No authenticated user session found"));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("id", currentUserId));

        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("id", id));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("username", username));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toUserResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("id", id));

        if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
                throw new UserAlreadyExistsException("email", updateUserRequest.getEmail());
            }
        }

        userMapper.updateEntityFromDto(updateUserRequest, user);
        User updatedUser = userRepository.save(user);

        log.info("User with ID: {} updated successfully", id);
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UserNotFoundException("No authenticated user session found"));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("id", currentUserId));

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password provided does not match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully for user ID: {}", currentUserId);
    }

    @Override
    @Transactional
    public UserResponse updateUserRole(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("id", id));

        user.setRole(role);
        User updated = userRepository.save(user);
        log.info("User ID: {} role updated to: {}", id, role);
        return userMapper.toUserResponse(updated);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("id", id));

        userRepository.delete(user);
        log.info("User with ID: {} deleted successfully", id);
    }
}

