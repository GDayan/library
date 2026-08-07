package com.example.library.dto;

import java.time.LocalDateTime;

public record ExpensesResponse (Long id, Long userId, String description, String category, Double amount, LocalDateTime createdAt){}
