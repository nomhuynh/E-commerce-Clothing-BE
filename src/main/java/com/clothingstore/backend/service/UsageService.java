package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Usage;

import java.util.List;

public interface UsageService {
    Usage create(Usage usage);
    Usage update(Usage usage);
    Usage getById(String id);
    Usage getByName(String name);
    List<Usage> getAll();
    void delete(String id);
}
