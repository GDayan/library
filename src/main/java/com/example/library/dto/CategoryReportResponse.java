package com.example.library.dto;


import java.util.List;

public record CategoryReportResponse(String category, Double sum, int count, List<ExpensesResponse> expensesResponses){ }
