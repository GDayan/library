package com.example.library.service;

import com.example.library.dto.CategoryReportResponse;
import com.example.library.dto.ExpensesRequest;
import com.example.library.dto.ExpensesResponse;
import com.example.library.entity.Expense;
import com.example.library.entity.User;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.ExpensesMapper;
import com.example.library.mapper.UserMapper;
import com.example.library.repository.ExpensesRepository;
import com.example.library.service.UserService;
import com.example.library.service.impl.ExpensesServiceImpl;
import com.example.library.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpensesServiceImplTest {

    @Mock
    private ExpensesMapper expensesMapper;

    @Mock
    private ExpensesRepository expensesRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ExpensesServiceImpl expensesService;

    private User user;
    private Expense expense;
    private ExpensesRequest expensesRequest;
    private ExpensesResponse expensesResponse;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("John Doe").build();

        expense = Expense.builder()
                .id(10L)
                .description("Groceries")
                .amount(45.99)
                .category("Food")
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        expensesRequest = new ExpensesRequest("Groceries", 45.99, "Food");
        expensesResponse = new ExpensesResponse(10L, 1L, "Groceries", "Food", 45.99, expense.getCreatedAt());
    }

    @Test
    void createExpenses_shouldAttachUserAndSave() {
        when(userService.findById(1L)).thenReturn(user);
        when(expensesMapper.toEntity(expensesRequest)).thenReturn(expense);
        when(expensesRepository.save(expense)).thenReturn(expense);
        when(expensesMapper.toResponse(expense)).thenReturn(expensesResponse);

        ExpensesResponse result = expensesService.createExpenses(1L, expensesRequest);

        assertThat(result).isEqualTo(expensesResponse);
        assertThat(expense.getUser()).isEqualTo(user);
        verify(expensesRepository).save(expense);
    }

    @Test
    void getAllByUserId_shouldReturnMappedList() {
        when(expensesRepository.findByUserId(1L)).thenReturn(List.of(expense));
        when(expensesMapper.toResponse(expense)).thenReturn(expensesResponse);

        List<ExpensesResponse> result = expensesService.getAllByUserId(1L);

        assertThat(result).containsExactly(expensesResponse);
    }

    @Test
    void getByIdAndUser_whenExists_shouldReturnResponse() {
        when(expensesRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(expense));
        when(expensesMapper.toResponse(expense)).thenReturn(expensesResponse);

        ExpensesResponse result = expensesService.getByIdAndUser(1L, 10L);

        assertThat(result).isEqualTo(expensesResponse);
    }

    @Test
    void getByIdAndUser_whenNotFound_shouldThrow() {
        when(expensesRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expensesService.getByIdAndUser(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByIdAndUser_whenBelongsToAnotherUser_shouldThrow() {
        // изоляция между пользователями: запрос с "чужим" userId не находит запись
        when(expensesRepository.findByIdAndUserId(10L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expensesService.getByIdAndUser(2L, 10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateExpenses_whenExists_shouldUpdateFieldsAndSave() {
        ExpensesRequest updateRequest = new ExpensesRequest("Updated", 99.99, "Transport");
        when(expensesRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(expense));
        when(expensesRepository.save(any(Expense.class))).thenReturn(expense);
        when(expensesMapper.toResponse(any(Expense.class))).thenReturn(
                new ExpensesResponse(10L, 1L, "Updated", "Transport", 99.99, expense.getCreatedAt()));

        ExpensesResponse result = expensesService.updateExpenses(1L, 10L, updateRequest);

        assertThat(result.description()).isEqualTo("Updated");
        assertThat(result.amount()).isEqualTo(99.99);
        assertThat(result.category()).isEqualTo("Transport");
        assertThat(expense.getDescription()).isEqualTo("Updated");
        verify(expensesRepository).save(expense);
    }

    @Test
    void updateExpenses_whenNotFound_shouldThrow() {
        when(expensesRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expensesService.updateExpenses(1L, 999L, expensesRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(expensesRepository, never()).save(any());
    }

    @Test
    void delete_whenExists_shouldDelete() {
        when(expensesRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(expense));

        expensesService.delete(1L, 10L);

        verify(expensesRepository).delete(expense);
    }

    @Test
    void delete_whenNotFound_shouldThrowAndNotDelete() {
        when(expensesRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expensesService.delete(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(expensesRepository, never()).delete(any());
    }

    @Test
    void getChronologicalReport_shouldReturnResultsOrderedByRepository() {
        LocalDateTime from = LocalDateTime.now().minusDays(7);
        LocalDateTime to = LocalDateTime.now();

        Expense newer = Expense.builder().id(11L).description("Newer").amount(10.0)
                .category("Food").user(user).createdAt(to).build();
        Expense older = Expense.builder().id(12L).description("Older").amount(20.0)
                .category("Food").user(user).createdAt(from).build();

        // репозиторий уже отвечает за сортировку (Order By ... Desc), сервис её не переопределяет
        when(expensesRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(1L, from, to))
                .thenReturn(List.of(newer, older));
        when(expensesMapper.toResponse(newer)).thenReturn(
                new ExpensesResponse(11L, 1L, "Newer", "Food", 10.0, to));
        when(expensesMapper.toResponse(older)).thenReturn(
                new ExpensesResponse(12L, 1L, "Older", "Food", 20.0, from));

        List<ExpensesResponse> result = expensesService.getChronologicalReport(1L, from, to);

        assertThat(result).extracting(ExpensesResponse::id).containsExactly(11L, 12L);
    }

    @Test
    void getCategoryReport_shouldGroupByCategoryWithCorrectSumAndCount() {
        LocalDateTime from = LocalDateTime.now().minusDays(30);
        LocalDateTime to = LocalDateTime.now();

        Expense food1 = Expense.builder().id(1L).description("Groceries").amount(50.0)
                .category("Food").user(user).createdAt(to).build();
        Expense food2 = Expense.builder().id(2L).description("Restaurant").amount(30.0)
                .category("Food").user(user).createdAt(to).build();
        Expense transport = Expense.builder().id(3L).description("Metro").amount(15.0)
                .category("Transport").user(user).createdAt(to).build();

        when(expensesRepository.findByUserIdAndCreatedAtBetween(1L, from, to))
                .thenReturn(List.of(food1, food2, transport));

        when(expensesMapper.toResponse(food1)).thenReturn(
                new ExpensesResponse(1L, 1L, "Groceries", "Food", 50.0, to));
        when(expensesMapper.toResponse(food2)).thenReturn(
                new ExpensesResponse(2L, 1L, "Restaurant", "Food", 30.0, to));
        when(expensesMapper.toResponse(transport)).thenReturn(
                new ExpensesResponse(3L, 1L, "Metro", "Transport", 15.0, to));

        List<CategoryReportResponse> result = expensesService.getCategoryReport(1L, from, to);

        assertThat(result).hasSize(2);

        CategoryReportResponse foodReport = result.stream()
                .filter(r -> r.category().equals("Food"))
                .findFirst().orElseThrow();
        assertThat(foodReport.sum()).isEqualTo(80.0);
        assertThat(foodReport.count()).isEqualTo(2);
        assertThat(foodReport.expensesResponses()).hasSize(2);

        CategoryReportResponse transportReport = result.stream()
                .filter(r -> r.category().equals("Transport"))
                .findFirst().orElseThrow();
        assertThat(transportReport.sum()).isEqualTo(15.0);
        assertThat(transportReport.count()).isEqualTo(1);
    }

    @Test
    void getCategoryReport_whenNoExpenses_shouldReturnEmptyList() {
        LocalDateTime from = LocalDateTime.now().minusDays(30);
        LocalDateTime to = LocalDateTime.now();

        when(expensesRepository.findByUserIdAndCreatedAtBetween(1L, from, to))
                .thenReturn(List.of());

        List<CategoryReportResponse> result = expensesService.getCategoryReport(1L, from, to);

        assertThat(result).isEmpty();
    }
}
