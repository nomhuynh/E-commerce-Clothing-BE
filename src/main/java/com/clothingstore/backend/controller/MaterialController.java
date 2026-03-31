package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.entity.Material;
import com.clothingstore.backend.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
@Tag(name = "Materials")
public class MaterialController {

    private final MaterialService materialService;

    @Operation(summary = "Get all materials")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Material>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Materials fetched", materialService.getAll()));
    }

    @Operation(summary = "Get material by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Material>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Material fetched", materialService.getById(id)));
    }

    @Operation(summary = "Create material")
    @PostMapping
    public ResponseEntity<ApiResponse<Material>> create(@RequestBody Material material) {
        return ResponseEntity.ok(ApiResponse.success("Material created", materialService.create(material)));
    }

    @Operation(summary = "Update material")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Material>> update(@PathVariable String id, @RequestBody Material material) {
        material.setId(id);
        return ResponseEntity.ok(ApiResponse.success("Material updated", materialService.update(material)));
    }

    @Operation(summary = "Delete material")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        materialService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Material deleted", null));
    }
}
