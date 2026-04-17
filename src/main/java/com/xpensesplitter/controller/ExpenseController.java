package com.xpensesplitter.controller;

import com.xpensesplitter.dto.request.AddExpenseRequest;
import com.xpensesplitter.dto.response.*;
import com.xpensesplitter.entity.Expense;
import com.xpensesplitter.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    // ✅ ADD EXPENSE
    @PostMapping
    public ResponseEntity<String> addExpense(@RequestBody AddExpenseRequest request) {

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        expenseService.addExpense(request, email);

        return ResponseEntity.ok("Expense added successfully");
    }

    // ✅ GET EXPENSES BY GROUP (🔥 IMPORTANT FOR UI)
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Expense>> getExpenses(@PathVariable Long groupId) {
        return ResponseEntity.ok(expenseService.getExpensesByGroup(groupId));
    }

    // ✅ MONTHLY ANALYTICS
    @GetMapping("/monthly/{groupId}")
    public ResponseEntity<List<MonthlySpending>> getMonthly(@PathVariable Long groupId) {
        return ResponseEntity.ok(expenseService.getMonthlySpending(groupId));
    }

    // ✅ USER NET BALANCES
    @GetMapping("/user-balances/{groupId}")
    public ResponseEntity<List<UserBalance>> getUserBalances(@PathVariable Long groupId) {
        return ResponseEntity.ok(expenseService.getUserBalances(groupId));
    }

    // ✅ FINAL BALANCES (WHO OWES WHOM)
    @GetMapping("/balances/{groupId}")
    public ResponseEntity<List<BalanceResponse>> getBalances(@PathVariable Long groupId) {
        return ResponseEntity.ok(expenseService.getBalances(groupId));
    }

    // ✅ SMART SETTLEMENT
    @GetMapping("/settle/{groupId}")
    public ResponseEntity<List<Settlement>> settle(@PathVariable Long groupId) {
        return ResponseEntity.ok(expenseService.simplifyExpenses(groupId));
    }
}