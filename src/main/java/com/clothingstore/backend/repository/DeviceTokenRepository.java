package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, String> {
    List<DeviceToken> findByUserIdAndIsActiveTrue(String userId);
}
