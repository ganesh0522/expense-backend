package com.xpensesplitter.repository;

import com.xpensesplitter.entity.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {

    // 🔥 REQUIRED FOR BALANCE LOGIC
    List<ExpenseSplit> findByExpenseIdIn(List<Long> expenseIds);
}