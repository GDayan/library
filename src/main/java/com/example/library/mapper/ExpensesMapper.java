package com.example.library.mapper;

import com.example.library.dto.ExpensesRequest;
import com.example.library.dto.ExpensesResponse;
import com.example.library.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpensesMapper {
    @Mapping(target = "userId", source = "user.id")
    ExpensesResponse toResponse(Expense expense);
    @Mapping(target = "user", ignore = true)
    Expense toEntity(ExpensesRequest expensesRequest);
}
