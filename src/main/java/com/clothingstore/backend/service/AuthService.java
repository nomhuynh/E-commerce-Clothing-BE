package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.auth.RegisterRequest;
import com.clothingstore.backend.entity.User;

public interface AuthService {
    User register(RegisterRequest request);
}
