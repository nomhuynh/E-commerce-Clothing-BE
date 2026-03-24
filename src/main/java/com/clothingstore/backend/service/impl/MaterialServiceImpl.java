package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Material;
import com.clothingstore.backend.repository.MaterialRepository;
import com.clothingstore.backend.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    @Override
    @Transactional
    public Material create(Material material) {
        if (existsByName(material.getName())) {
            throw new RuntimeException("Material name already exists");
        }
        return materialRepository.save(material);
    }

    @Override
    @Transactional
    public Material update(Material material) {
        Material existing = materialRepository.findById(material.getId())
                .orElseThrow(() -> new RuntimeException("Material not found"));
        
        if (material.getName() != null) {
            existing.setName(material.getName());
        }
        if (material.getDescription() != null) {
            existing.setDescription(material.getDescription());
        }
        
        return materialRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found"));
        materialRepository.delete(material);
    }

    @Override
    public Material getById(String id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found"));
    }

    @Override
    public List<Material> getAll() {
        return materialRepository.findAll();
    }

    @Override
    public boolean existsByName(String name) {
        return materialRepository.existsByName(name);
    }
}