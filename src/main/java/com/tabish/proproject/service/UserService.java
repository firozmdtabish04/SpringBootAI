package com.tabish.proproject.service;

import com.tabish.proproject.dto.request.ChangePasswordRequest;
import com.tabish.proproject.dto.request.UpdateUserRequest;
import com.tabish.proproject.dto.response.UserResponse;
import com.tabish.proproject.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse getCurrentUserProfile();

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest);

    void changePassword(ChangePasswordRequest changePasswordRequest);

    UserResponse updateUserRole(Long id, Role role);

    void deleteUser(Long id);
}

