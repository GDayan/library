package com.example.library.controller;

import com.example.library.dto.CategoryReportResponse;
import com.example.library.dto.ExpensesRequest;
import com.example.library.dto.ExpensesResponse;
import com.example.library.service.ExpensesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/{userId}/expenses")
public class ExpensesController {
    private final ExpensesService expensesService;

    @PostMapping
    public ResponseEntity<ExpensesResponse> createExpenses(@PathVariable Long userId,
                                                           @Valid @RequestBody ExpensesRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(expensesService.createExpenses(userId, request));
    }

    @GetMapping("{expenseId}")
    public ResponseEntity<ExpensesResponse> findById(@PathVariable Long userId, @PathVariable Long expenseId){
        return ResponseEntity.ok(expensesService.getByIdAndUser(userId, expenseId));
    }

    @GetMapping
    public ResponseEntity<List<ExpensesResponse>> findAll(@PathVariable Long userId){
        return ResponseEntity.ok(expensesService.getAllByUserId(userId));
    }

    @PutMapping("{expenseId}")
    public ResponseEntity<ExpensesResponse> updateExpenses(@PathVariable Long userId, @PathVariable Long expenseId,
                                                           @Valid @RequestBody ExpensesRequest request){
        return ResponseEntity.ok(expensesService.updateExpenses(userId, expenseId, request));
    }

    @DeleteMapping("{expenseId}")
    public ResponseEntity<Void> deleteExpenses(@PathVariable Long userId, @PathVariable Long expenseId){
        expensesService.delete(userId, expenseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/report/chronological")
    public ResponseEntity<List<ExpensesResponse>> chronologicalReport(
            @PathVariable Long userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to){
        return ResponseEntity.ok(expensesService.getChronologicalReport(userId, from, to));
    }

    @GetMapping("/report/by-category")
    public ResponseEntity<List<CategoryReportResponse>> categoryReport(
            @PathVariable Long userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to){
        return ResponseEntity.ok(expensesService.getCategoryReport(userId, from, to));
    }
}