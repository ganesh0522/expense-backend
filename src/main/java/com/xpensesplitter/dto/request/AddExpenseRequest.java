package com.xpensesplitter.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AddExpenseRequest {

    private Long groupId;
    private Double amount;
    private String description;

    private String splitType; // EQUAL or CUSTOM

    private List<SplitUser> splits;

    @Data
    public static class SplitUser {
        private Long userId;
        private Double amount;
        private Double percentage;
    }
}
