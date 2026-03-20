package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Color;
import com.clothingstore.backend.repository.ColorRepository;
import com.clothingstore.backend.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;

    @Override
    public Color create(Color color) {
        if (colorRepository.existsByName(color.getName())) {
            throw new RuntimeException("Color name already exists");
        }
        return colorRepository.save(color);
    }

    @Override
    public Color update(Color color) {
        if (color.getId() == null) {
            throw new RuntimeException("Color id is required for update");
        }
        if (!colorRepository.existsById(color.getId())) {
            throw new RuntimeException("Color not found");
        }
        return colorRepository.save(color);
    }

    @Override
    public Color getById(String id) {
        return colorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Color not found"));
    }

    @Override
    public Color getByName(String name) {
        return colorRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Color not found"));
    }

    @Override
    public List<Color> getAll() {
        return colorRepository.findAll();
    }

    @Override
    public void delete(String id) {
        if (!colorRepository.existsById(id)) {
            throw new RuntimeException("Color not found");
        }
        colorRepository.deleteById(id);
    }
}
