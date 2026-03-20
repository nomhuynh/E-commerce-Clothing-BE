package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Usage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsageRepository extends JpaRepository<Usage, String> {
    Optional<Usage> findByName(String name);
    Boolean existsByName(String name);
}
