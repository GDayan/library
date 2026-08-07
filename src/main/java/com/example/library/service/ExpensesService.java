package com.example.library.service;

import com.example.library.dto.CategoryReportResponse;
import com.example.library.dto.ExpensesRequest;
import com.example.library.dto.ExpensesResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpensesService {
    ExpensesResponse createExpenses(Long userId, ExpensesRequest request);
    List<ExpensesResponse> getAllByUserId(Long userId);
    ExpensesResponse getByIdAndUser(Long userId, Long expenseId);
    ExpensesResponse updateExpenses(Long userId, Long expenseId, ExpensesRequest request);
    void delete(Long userId, Long expenseId);

    List<ExpensesResponse> getChronologicalReport(Long userId, LocalDateTime from, LocalDateTime to);
    List<CategoryReportResponse> getCategoryReport(Long userId, LocalDateTime from, LocalDateTime to);
}
