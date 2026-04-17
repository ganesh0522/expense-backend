package com.xpensesplitter.repository;

import com.xpensesplitter.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // ✅ Fetch expenses by group (VERY IMPORTANT)
    List<Expense> findByGroupId(Long groupId);
}