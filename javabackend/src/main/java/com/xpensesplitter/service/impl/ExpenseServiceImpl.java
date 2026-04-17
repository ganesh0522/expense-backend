package com.xpensesplitter.service.impl;

import com.xpensesplitter.dto.request.AddExpenseRequest;
import com.xpensesplitter.dto.response.*;
import com.xpensesplitter.entity.Expense;
import com.xpensesplitter.entity.ExpenseSplit;
import com.xpensesplitter.entity.User;
import com.xpensesplitter.repository.ExpenseRepository;
import com.xpensesplitter.repository.ExpenseSplitRepository;
import com.xpensesplitter.repository.UserRepository;
import com.xpensesplitter.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository splitRepository;
    private final UserRepository userRepository;

    // ✅ ADD EXPENSE
    @Override
    public void addExpense(AddExpenseRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = Expense.builder()
                .groupId(request.getGroupId())
                .paidBy(user.getId())
                .amount(request.getAmount())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        expenseRepository.save(expense);

        // 🔥 EQUAL SPLIT
        if ("EQUAL".equalsIgnoreCase(request.getSplitType())) {

            int totalUsers = request.getSplits().size();
            double splitAmount = request.getAmount() / totalUsers;

            for (AddExpenseRequest.SplitUser splitUser : request.getSplits()) {
                splitRepository.save(
                        ExpenseSplit.builder()
                                .expenseId(expense.getId())
                                .userId(splitUser.getUserId())
                                .amount(splitAmount)
                                .build()
                );
            }
        }

        // 🔥 CUSTOM SPLIT
        else if ("CUSTOM".equalsIgnoreCase(request.getSplitType())) {

            double total = request.getSplits().stream()
                    .mapToDouble(AddExpenseRequest.SplitUser::getAmount)
                    .sum();

            if (Math.abs(total - request.getAmount()) > 0.01) {
                throw new RuntimeException("Split amounts must equal total");
            }

            for (AddExpenseRequest.SplitUser split : request.getSplits()) {
                splitRepository.save(
                        ExpenseSplit.builder()
                                .expenseId(expense.getId())
                                .userId(split.getUserId())
                                .amount(split.getAmount())
                                .build()
                );
            }
        }

        // 🔥 PERCENTAGE SPLIT
        else if ("PERCENTAGE".equalsIgnoreCase(request.getSplitType())) {

            double totalPercentage = request.getSplits().stream()
                    .mapToDouble(AddExpenseRequest.SplitUser::getPercentage)
                    .sum();

            if (Math.abs(totalPercentage - 100.0) > 0.01) {
                throw new RuntimeException("Total percentage must be 100");
            }

            for (AddExpenseRequest.SplitUser split : request.getSplits()) {

                double amount = (split.getPercentage() / 100.0) * request.getAmount();

                splitRepository.save(
                        ExpenseSplit.builder()
                                .expenseId(expense.getId())
                                .userId(split.getUserId())
                                .amount(amount)
                                .build()
                );
            }
        }
    }

    // ✅ FETCH EXPENSES
    @Override
    public List<Expense> getExpensesByGroup(Long groupId) {
        return expenseRepository.findByGroupId(groupId);
    }

    // 🔥 COMMON BALANCE LOGIC
    private Map<Long, Double> calculateBalances(Long groupId) {

        List<Expense> expenses = expenseRepository.findByGroupId(groupId);

        List<Long> expenseIds = expenses.stream()
                .map(Expense::getId)
                .toList();

        List<ExpenseSplit> splits = splitRepository.findByExpenseIdIn(expenseIds);

        Map<Long, Double> balanceMap = new HashMap<>();

        // CREDIT
        for (Expense e : expenses) {
            balanceMap.put(
                    e.getPaidBy(),
                    balanceMap.getOrDefault(e.getPaidBy(), 0.0) + e.getAmount()
            );
        }

        // DEBIT
        for (ExpenseSplit s : splits) {
            balanceMap.put(
                    s.getUserId(),
                    balanceMap.getOrDefault(s.getUserId(), 0.0) - s.getAmount()
            );
        }

        return balanceMap;
    }

    // ✅ USER BALANCES
    @Override
    public List<UserBalance> getUserBalances(Long groupId) {

        Map<Long, Double> balanceMap = calculateBalances(groupId);

        return balanceMap.entrySet()
                .stream()
                .map(e -> new UserBalance(e.getKey(), e.getValue()))
                .toList();
    }

    // ✅ BALANCES (WHO OWES WHOM)
    @Override
    public List<BalanceResponse> getBalances(Long groupId) {

        Map<Long, Double> balanceMap = calculateBalances(groupId);

        List<Map.Entry<Long, Double>> creditors = new ArrayList<>();
        List<Map.Entry<Long, Double>> debtors = new ArrayList<>();

        for (Map.Entry<Long, Double> entry : balanceMap.entrySet()) {
            if (entry.getValue() > 0.01) creditors.add(entry);
            else if (entry.getValue() < -0.01) debtors.add(entry);
        }

        List<BalanceResponse> result = new ArrayList<>();

        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {

            double debt = -debtors.get(i).getValue();
            double credit = creditors.get(j).getValue();

            double settle = Math.min(debt, credit);

            result.add(new BalanceResponse(
                    debtors.get(i).getKey(),
                    creditors.get(j).getKey(),
                    settle
            ));

            debtors.get(i).setValue(debtors.get(i).getValue() + settle);
            creditors.get(j).setValue(creditors.get(j).getValue() - settle);

            if (Math.abs(debtors.get(i).getValue()) < 0.01) i++;
            if (Math.abs(creditors.get(j).getValue()) < 0.01) j++;
        }

        return result;
    }

    // ✅ SMART SETTLEMENT
    @Override
    public List<Settlement> simplifyExpenses(Long groupId) {

        Map<Long, Double> balanceMap = calculateBalances(groupId);

        PriorityQueue<Map.Entry<Long, Double>> creditors =
                new PriorityQueue<>((a, b) -> Double.compare(b.getValue(), a.getValue()));

        PriorityQueue<Map.Entry<Long, Double>> debtors =
                new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));

        for (Map.Entry<Long, Double> entry : balanceMap.entrySet()) {
            if (entry.getValue() > 0) creditors.add(entry);
            else if (entry.getValue() < 0) debtors.add(entry);
        }

        List<Settlement> result = new ArrayList<>();

        while (!creditors.isEmpty() && !debtors.isEmpty()) {

            Map.Entry<Long, Double> creditor = creditors.poll();
            Map.Entry<Long, Double> debtor = debtors.poll();

            double settle = Math.min(creditor.getValue(), -debtor.getValue());

            result.add(new Settlement(
                    debtor.getKey(),
                    creditor.getKey(),
                    settle
            ));

            double creditLeft = creditor.getValue() - settle;
            double debtLeft = debtor.getValue() + settle;

            if (creditLeft > 0) {
                creditors.add(new AbstractMap.SimpleEntry<>(creditor.getKey(), creditLeft));
            }

            if (debtLeft < 0) {
                debtors.add(new AbstractMap.SimpleEntry<>(debtor.getKey(), debtLeft));
            }
        }

        return result;
    }

    // ✅ MONTHLY SPENDING
    @Override
    public List<MonthlySpending> getMonthlySpending(Long groupId) {

        List<Expense> expenses = expenseRepository.findByGroupId(groupId);

        Map<String, Double> monthlyMap = new HashMap<>();

        for (Expense e : expenses) {
            String month = e.getCreatedAt().getMonth().toString();

            monthlyMap.put(
                    month,
                    monthlyMap.getOrDefault(month, 0.0) + e.getAmount()
            );
        }

        return monthlyMap.entrySet()
                .stream()
                .map(entry -> new MonthlySpending(entry.getKey(), entry.getValue()))
                .toList();
    }
}