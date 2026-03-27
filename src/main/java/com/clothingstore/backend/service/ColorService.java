package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Color;

import java.util.List;

public interface ColorService {
    Color create(Color color);
    Color update(Color color);
    Color getById(String id);
    Color getByName(String name);
    List<Color> getAll();
    void delete(String id);
}
