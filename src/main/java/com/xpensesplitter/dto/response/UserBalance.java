package com.xpensesplitter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBalance {
    private Long userId;
    private Double balance;
}
