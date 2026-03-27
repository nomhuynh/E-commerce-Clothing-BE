package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Size;
import com.clothingstore.backend.entity.enums.SizeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SizeRepository extends JpaRepository<Size, String> {
    Optional<Size> findByNameAndType(String name, SizeType type);
    Boolean existsByNameAndType(String name, SizeType type);
}
