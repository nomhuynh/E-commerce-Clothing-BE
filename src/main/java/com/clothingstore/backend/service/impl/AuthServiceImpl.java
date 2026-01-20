package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.auth.RegisterRequest;
import com.clothingstore.backend.entity.AuthProvider;
import com.clothingstore.backend.entity.Role;
import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.repository.UserRepository;
import com.clothingstore.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.USER)
                .provider(AuthProvider.LOCAL)
                .isBlock(false)
                .build();

        return userRepository.save(user);
    }
}
