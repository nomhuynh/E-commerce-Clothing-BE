package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Size;
import com.clothingstore.backend.entity.enums.SizeType;
import com.clothingstore.backend.repository.SizeRepository;
import com.clothingstore.backend.service.SizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

    private final SizeRepository sizeRepository;

    @Override
    @Transactional
    public Size create(Size size) {
        if (existsByNameAndType(size.getName(), size.getType())) {
            throw new RuntimeException("Size with this name and type already exists");
        }
        return sizeRepository.save(size);
    }

    @Override
    @Transactional
    public Size update(Size size) {
        Size existing = sizeRepository.findById(size.getId())
                .orElseThrow(() -> new RuntimeException("Size not found"));
        
        if (size.getName() != null) {
            existing.setName(size.getName());
        }
        if (size.getType() != null) {
            existing.setType(size.getType());
        }
        
        return sizeRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found"));
        sizeRepository.delete(size);
    }

    @Override
    public Size getById(String id) {
        return sizeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Size not found"));
    }

    @Override
    public List<Size> getAll() {
        return sizeRepository.findAll();
    }

    @Override
    public boolean existsByNameAndType(String name, SizeType type) {
        return sizeRepository.existsByNameAndType(name, type);
    }
}