package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Size;

import java.util.List;

public interface SizeService {
    Size create(Size size);
    Size update(Size size);
    void delete(String id);
    Size getById(String id);
    List<Size> getAll();
    boolean existsByNameAndType(String name, com.clothingstore.backend.entity.enums.SizeType type);
}