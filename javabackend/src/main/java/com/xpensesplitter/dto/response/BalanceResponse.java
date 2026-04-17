package com.xpensesplitter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BalanceResponse {
    private Long fromUser;
    private Long toUser;
    private Double amount;
}