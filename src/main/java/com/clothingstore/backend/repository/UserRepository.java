package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Boolean existsByPhoneNumber(String phoneNumber);
}
