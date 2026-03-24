package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Material;

import java.util.List;

public interface MaterialService {
    Material create(Material material);
    Material update(Material material);
    void delete(String id);
    Material getById(String id);
    List<Material> getAll();
    boolean existsByName(String name);
}