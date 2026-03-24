package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findBySlug(String slug);
    Boolean existsBySlug(String slug);
    List<Category> findByParentId(String parentId);
}
