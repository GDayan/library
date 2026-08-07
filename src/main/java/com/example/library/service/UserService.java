package com.example.library.service;

import com.example.library.dto.UserRequest;
import com.example.library.dto.UserResponse;
import com.example.library.entity.User;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);
    UserResponse getById(Long id);
    User findById(Long id);
    List<UserResponse> getAll();
    UserResponse updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);
}
