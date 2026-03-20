package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Size;
import com.clothingstore.backend.entity.enums.SizeType;

import java.util.List;

public interface SizeService {
    Size create(Size size);
    Size update(Size size);
    Size getById(String id);
    Size getByNameAndType(String name, SizeType type);
    List<Size> getAll();
    void delete(String id);
}
