package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Material;

import java.util.List;

public interface MaterialService {
    Material create(Material material);
    Material update(Material material);
    Material getById(String id);
    Material getByName(String name);
    List<Material> getAll();
    void delete(String id);
}
