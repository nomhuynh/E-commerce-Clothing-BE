package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.color.ColorRequest;
import com.clothingstore.backend.dto.color.ColorResponse;
import com.clothingstore.backend.entity.Color;
import com.clothingstore.backend.service.ColorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/colors")
@RequiredArgsConstructor
public class ColorController {

    private final ColorService colorService;

    @PostMapping
    public ResponseEntity<ApiResponse<ColorResponse>> create(@Valid @RequestBody ColorRequest request) {
        Color color = toEntity(request);
        Color created = colorService.create(color);
        return ResponseEntity.ok(ApiResponse.success("Color created", toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ColorResponse>> update(@PathVariable String id, @Valid @RequestBody ColorRequest request) {
        Color color = toEntity(request);
        color.setId(id);
        Color updated = colorService.update(color);
        return ResponseEntity.ok(ApiResponse.success("Color updated", toResponse(updated)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ColorResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Color fetched", toResponse(colorService.getById(id))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ColorResponse>>> getAll() {
        List<ColorResponse> data = colorService.getAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Colors fetched", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        colorService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Color deleted", null));
    }

    private Color toEntity(ColorRequest request) {
        Color color = new Color();
        color.setName(request.getName());
        color.setHexCode(request.getHexCode());
        return color;
    }

    private ColorResponse toResponse(Color color) {
        return ColorResponse.builder()
                .id(color.getId())
                .name(color.getName())
                .hexCode(color.getHexCode())
                .createdAt(color.getCreatedAt())
                .updatedAt(color.getUpdatedAt())
                .build();
    }
}