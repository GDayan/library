package com.example.library.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ExpensesRequest (
    @NotNull(message = "Description is required") String description,
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount is positive")
    Double amount,

    @NotNull(message = "Categories is required")
    String category){}
