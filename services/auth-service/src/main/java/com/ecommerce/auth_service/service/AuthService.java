package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login (LoginRequest request);
}
