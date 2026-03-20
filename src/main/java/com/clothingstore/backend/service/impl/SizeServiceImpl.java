package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Size;
import com.clothingstore.backend.entity.enums.SizeType;
import com.clothingstore.backend.repository.SizeRepository;
import com.clothingstore.backend.service.SizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

    private final SizeRepository sizeRepository;

    @Override
    public Size create(Size size) {
        if (sizeRepository.existsByNameAndType(size.getName(), size.getType())) {
            throw new RuntimeException("Size already exists for type");
        }
        return sizeRepository.save(size);
    }

    @Override
    public Size update(Size size) {
        if (size.getId() == null) {
            throw new RuntimeException("Size id is required for update");
        }
        if (!sizeRepository.existsById(size.getId())) {
            throw new RuntimeException("Size not found");
        }
        return sizeRepository.save(size);
    }

    @Override
    public Size getById(String id) {
        return sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found"));
    }

    @Override
    public Size getByNameAndType(String name, SizeType type) {
        return sizeRepository.findByNameAndType(name, type)
                .orElseThrow(() -> new RuntimeException("Size not found"));
    }

    @Override
    public List<Size> getAll() {
        return sizeRepository.findAll();
    }

    @Override
    public void delete(String id) {
        if (!sizeRepository.existsById(id)) {
            throw new RuntimeException("Size not found");
        }
        sizeRepository.deleteById(id);
    }
}
