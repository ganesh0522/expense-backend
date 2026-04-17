package com.xpensesplitter.service;

import com.xpensesplitter.dto.request.*;
import com.xpensesplitter.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
}