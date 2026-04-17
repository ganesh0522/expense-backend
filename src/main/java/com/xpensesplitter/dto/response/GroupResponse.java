package com.xpensesplitter.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class GroupResponse {

    private Long id;
    private String name;

    // 🔥 ADD THIS
    private List<UserResponse> members;

    public GroupResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}