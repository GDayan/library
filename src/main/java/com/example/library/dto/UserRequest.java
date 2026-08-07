package com.example.library.dto;

import jakarta.validation.constraints.NotNull;

public record UserRequest(@NotNull(message = "User is required") String name){}
