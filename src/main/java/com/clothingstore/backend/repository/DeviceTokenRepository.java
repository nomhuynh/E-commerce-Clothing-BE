package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, String> {
    List<DeviceToken> findByUserId(String userId);
    Optional<DeviceToken> findByUserIdAndToken(String userId, String token);
}
