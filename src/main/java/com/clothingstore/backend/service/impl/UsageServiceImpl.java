package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Usage;
import com.clothingstore.backend.repository.UsageRepository;
import com.clothingstore.backend.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

    private final UsageRepository usageRepository;

    @Override
    @Transactional
    public Usage create(Usage usage) {
        if (existsByName(usage.getName())) {
            throw new RuntimeException("Usage name already exists");
        }
        return usageRepository.save(usage);
    }

    @Override
    @Transactional
    public Usage update(Usage usage) {
        Usage existing = usageRepository.findById(usage.getId())
                .orElseThrow(() -> new RuntimeException("Usage not found"));
        
        if (usage.getName() != null) {
            existing.setName(usage.getName());
        }
        if (usage.getDescription() != null) {
            existing.setDescription(usage.getDescription());
        }
        
        return usageRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Usage usage = usageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usage not found"));
        usageRepository.delete(usage);
    }

    @Override
    public Usage getById(String id) {
        return usageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usage not found"));
    }

    @Override
    public List<Usage> getAll() {
        return usageRepository.findAll();
    }

    @Override
    public boolean existsByName(String name) {
        return usageRepository.existsByName(name);
    }
}