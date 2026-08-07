package com.example.library.dto;

import com.example.library.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(Long id, String name, LocalDateTime createdAt) {

}
