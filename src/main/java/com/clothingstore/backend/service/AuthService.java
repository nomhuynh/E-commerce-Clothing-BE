package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.auth.*;
import com.clothingstore.backend.entity.User;

public interface AuthService {
    User register(RegisterRequest request);
    AuthLoginResponse login(LoginRequest request);
    void changePassword(ChangePasswordRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    AuthLoginResponse loginWithGoogle(GoogleLoginRequest request);
}
