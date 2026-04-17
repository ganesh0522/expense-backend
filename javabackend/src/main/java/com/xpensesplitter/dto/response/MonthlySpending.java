package com.xpensesplitter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlySpending {
    private String month;
    private Double total;
}
