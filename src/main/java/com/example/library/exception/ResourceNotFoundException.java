package com.example.library.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(Long expenseId){
        super(String.format("Expense is not found with id: %d", expenseId));
    }

    public ResourceNotFoundException(Long userId, Long expenseId){
        super(String.format("Expense with id=%d not belong to user with id=%d", expenseId, userId));
    }
}
