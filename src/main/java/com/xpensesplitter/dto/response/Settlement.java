package com.xpensesplitter.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Settlement {

    private Long fromUser;
    private Long toUser;
    private Double amount;
}