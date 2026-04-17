package com.xpensesplitter.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank
    private String name;

    @Email
    private String email;

    @Size(min = 6)
    private String password;
}