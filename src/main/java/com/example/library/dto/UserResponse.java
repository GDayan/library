package com.example.library.dto;


import java.time.LocalDateTime;

public record UserResponse(Long id, String name, LocalDateTime createdAt) {

}
