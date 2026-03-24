package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, String> {
    Boolean existsByName(String name);
}