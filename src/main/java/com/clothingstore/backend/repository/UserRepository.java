package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<User> findByPhone(String phone);

    Boolean existsByPhone(String phone);

    Optional<User> findByFullName(String fullName);

    Boolean existsByFullName(String fullName);
}
