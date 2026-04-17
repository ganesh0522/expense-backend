package com.xpensesplitter.service;

import com.xpensesplitter.dto.request.AddExpenseRequest;
import com.xpensesplitter.dto.response.*;
import com.xpensesplitter.entity.Expense;

import java.util.List;

public interface ExpenseService {

    void addExpense(AddExpenseRequest request, String userEmail);

    List<MonthlySpending> getMonthlySpending(Long groupId);

    List<UserBalance> getUserBalances(Long groupId);

    List<Settlement> simplifyExpenses(Long groupId);

    // 🔥 ADD THIS
    List<BalanceResponse> getBalances(Long groupId);

    List<Expense> getExpensesByGroup(Long groupId);
}