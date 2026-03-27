package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, String> {
    Optional<Material> findByName(String name);
    Boolean existsByName(String name);
}
