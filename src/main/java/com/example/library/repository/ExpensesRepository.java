package com.example.library.repository;

import com.example.library.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpensesRepository extends JpaRepository<Expense, Long> {
    List<Expense>  findByUserId(Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    List<Expense> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long userId, LocalDateTime from, LocalDateTime to);

    List<Expense> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime from, LocalDateTime to);
}
