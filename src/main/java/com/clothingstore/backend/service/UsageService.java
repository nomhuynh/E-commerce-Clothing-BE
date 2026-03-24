package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Usage;

import java.util.List;

public interface UsageService {
    Usage create(Usage usage);
    Usage update(Usage usage);
    void delete(String id);
    Usage getById(String id);
    List<Usage> getAll();
    boolean existsByName(String name);
}