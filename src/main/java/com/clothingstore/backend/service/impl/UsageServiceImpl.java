package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Usage;
import com.clothingstore.backend.repository.UsageRepository;
import com.clothingstore.backend.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

    private final UsageRepository usageRepository;

    @Override
    public Usage create(Usage usage) {
        if (usageRepository.existsByName(usage.getName())) {
            throw new RuntimeException("Usage name already exists");
        }
        return usageRepository.save(usage);
    }

    @Override
    public Usage update(Usage usage) {
        if (usage.getId() == null) {
            throw new RuntimeException("Usage id is required for update");
        }
        if (!usageRepository.existsById(usage.getId())) {
            throw new RuntimeException("Usage not found");
        }
        return usageRepository.save(usage);
    }

    @Override
    public Usage getById(String id) {
        return usageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usage not found"));
    }

    @Override
    public Usage getByName(String name) {
        return usageRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Usage not found"));
    }

    @Override
    public List<Usage> getAll() {
        return usageRepository.findAll();
    }

    @Override
    public void delete(String id) {
        if (!usageRepository.existsById(id)) {
            throw new RuntimeException("Usage not found");
        }
        usageRepository.deleteById(id);
    }
}
