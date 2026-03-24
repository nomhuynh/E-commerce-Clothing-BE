package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Color;
import com.clothingstore.backend.repository.ColorRepository;
import com.clothingstore.backend.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;

    @Override
    @Transactional
    public Color create(Color color) {
        if (existsByName(color.getName())) {
            throw new RuntimeException("Color name already exists");
        }
        return colorRepository.save(color);
    }

    @Override
    @Transactional
    public Color update(Color color) {
        Color existing = colorRepository.findById(color.getId())
                .orElseThrow(() -> new RuntimeException("Color not found"));
        
        if (color.getName() != null && !color.getName().equals(existing.getName())) {
            if (existsByName(color.getName())) {
                throw new RuntimeException("Color name already exists");
            }
            existing.setName(color.getName());
        }
        if (color.getHexCode() != null) {
            existing.setHexCode(color.getHexCode());
        }
        
        return colorRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found"));
        colorRepository.delete(color);
    }

    @Override
    public Color getById(String id) {
        return colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found"));
    }

    @Override
    public List<Color> getAll() {
        return colorRepository.findAll();
    }

    @Override
    public boolean existsByName(String name) {
        return colorRepository.existsByName(name);
    }
}