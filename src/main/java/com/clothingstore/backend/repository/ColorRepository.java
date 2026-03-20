package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, String> {
    Optional<Color> findByName(String name);
    Boolean existsByName(String name);
}
