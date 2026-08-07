package com.example.library.service.impl;

import com.example.library.dto.CategoryReportResponse;
import com.example.library.dto.ExpensesRequest;
import com.example.library.dto.ExpensesResponse;
import com.example.library.entity.Expense;
import com.example.library.entity.User;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.ExpensesMapper;
import com.example.library.mapper.UserMapper;
import com.example.library.repository.ExpensesRepository;
import com.example.library.service.ExpensesService;
import com.example.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpensesServiceImpl implements ExpensesService {
    private final ExpensesMapper expensesMapper;
    private final ExpensesRepository expensesRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public ExpensesResponse createExpenses(Long userId, ExpensesRequest expensesRequest) {
        User user = userService.findById(userId);
        Expense expense = expensesMapper.toEntity(expensesRequest);
        expense.setUser(user);

        return expensesMapper.toResponse(expensesRepository.save(expense));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExpensesResponse> getAllByUserId(Long userId){
        return expensesRepository.findByUserId(userId).stream().map(expensesMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ExpensesResponse getByIdAndUser(Long userId, Long expenseId){
        Expense expense = expensesRepository
                .findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId, expenseId));
        return expensesMapper.toResponse(expense);
    }

    @Override
    public ExpensesResponse updateExpenses(Long userId, Long expenseId, ExpensesRequest request) {
        Expense expense = expensesRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId, expenseId));
        expense.setDescription(request.description());
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        return expensesMapper.toResponse(expensesRepository.save(expense));
    }

    @Override
    public void delete(Long userId, Long expenseId) {
        Expense expense = expensesRepository
                .findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId, expenseId));

        expensesRepository.delete(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpensesResponse> getChronologicalReport(Long userId, LocalDateTime from, LocalDateTime to) {
        return expensesRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, from, to)
                .stream()
                .map(expensesMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryReportResponse> getCategoryReport(Long userId, LocalDateTime from, LocalDateTime to) {
        List<Expense> expenses = expensesRepository.findByUserIdAndCreatedAtBetween(userId, from, to);

        return expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory))
                .entrySet().stream()
                .map(entry -> {
                    List<ExpensesResponse> categoryExpenses = entry.getValue().stream()
                            .map(expensesMapper::toResponse)
                            .toList();
                    double sum = entry.getValue().stream()
                            .mapToDouble(Expense::getAmount)
                            .sum();
                    return new CategoryReportResponse(
                            entry.getKey(),
                            sum,
                            categoryExpenses.size(),
                            categoryExpenses
                    );
                })
                .toList();
    }
}
