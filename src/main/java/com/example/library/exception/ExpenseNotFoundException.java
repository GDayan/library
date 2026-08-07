package com.example.library.exception;

public class ExpenseNotFoundException extends RuntimeException{
    public ExpenseNotFoundException(Long id){
        super(String.format("Expres is not found with id: %d", id));
    }
}
