package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Color;

import java.util.List;

public interface ColorService {
    Color create(Color color);
    Color update(Color color);
    void delete(String id);
    Color getById(String id);
    List<Color> getAll();
    boolean existsByName(String name);
}