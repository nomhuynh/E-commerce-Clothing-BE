package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.material.MaterialRequest;
import com.clothingstore.backend.dto.material.MaterialResponse;
import com.clothingstore.backend.entity.Material;
import com.clothingstore.backend.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping
    public ResponseEntity<ApiResponse<MaterialResponse>> create(@Valid @RequestBody MaterialRequest request) {
        Material material = toEntity(request);
        Material created = materialService.create(material);
        return ResponseEntity.ok(ApiResponse.success("Material created", toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialResponse>> update(@PathVariable String id, @Valid @RequestBody MaterialRequest request) {
        Material material = toEntity(request);
        material.setId(id);
        Material updated = materialService.update(material);
        return ResponseEntity.ok(ApiResponse.success("Material updated", toResponse(updated)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Material fetched", toResponse(materialService.getById(id))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MaterialResponse>>> getAll() {
        List<MaterialResponse> data = materialService.getAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Materials fetched", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        materialService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Material deleted", null));
    }

    private Material toEntity(MaterialRequest request) {
        Material material = new Material();
        material.setName(request.getName());
        material.setDescription(request.getDescription());
        return material;
    }

    private MaterialResponse toResponse(Material material) {
        return MaterialResponse.builder()
                .id(material.getId())
                .name(material.getName())
                .description(material.getDescription())
                .createdAt(material.getCreatedAt())
                .updatedAt(material.getUpdatedAt())
                .build();
    }
}