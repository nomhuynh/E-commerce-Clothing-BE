package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color, String> {
    Boolean existsByName(String name);
}