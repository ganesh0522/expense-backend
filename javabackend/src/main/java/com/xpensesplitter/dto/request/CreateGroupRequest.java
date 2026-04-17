package com.xpensesplitter.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateGroupRequest {
    private String name;
    private List<Long> memberIds;
}
