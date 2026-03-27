package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Material;
import com.clothingstore.backend.repository.MaterialRepository;
import com.clothingstore.backend.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    @Override
    public Material create(Material material) {
        if (materialRepository.existsByName(material.getName())) {
            throw new RuntimeException("Material name already exists");
        }
        return materialRepository.save(material);
    }

    @Override
    public Material update(Material material) {
        if (material.getId() == null) {
            throw new RuntimeException("Material id is required for update");
        }
        if (!materialRepository.existsById(material.getId())) {
            throw new RuntimeException("Material not found");
        }
        return materialRepository.save(material);
    }

    @Override
    public Material getById(String id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found"));
    }

    @Override
    public Material getByName(String name) {
        return materialRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Material not found"));
    }

    @Override
    public List<Material> getAll() {
        return materialRepository.findAll();
    }

    @Override
    public void delete(String id) {
        if (!materialRepository.existsById(id)) {
            throw new RuntimeException("Material not found");
        }
        materialRepository.deleteById(id);
    }
}
