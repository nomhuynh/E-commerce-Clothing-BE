package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.size.SizeRequest;
import com.clothingstore.backend.dto.size.SizeResponse;
import com.clothingstore.backend.entity.Size;
import com.clothingstore.backend.service.SizeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sizes")
@RequiredArgsConstructor
public class SizeController {

    private final SizeService sizeService;

    @PostMapping
    public ResponseEntity<ApiResponse<SizeResponse>> create(@Valid @RequestBody SizeRequest request) {
        Size size = toEntity(request);
        Size created = sizeService.create(size);
        return ResponseEntity.ok(ApiResponse.success("Size created", toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SizeResponse>> update(@PathVariable String id, @Valid @RequestBody SizeRequest request) {
        Size size = toEntity(request);
        size.setId(id);
        Size updated = sizeService.update(size);
        return ResponseEntity.ok(ApiResponse.success("Size updated", toResponse(updated)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SizeResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Size fetched", toResponse(sizeService.getById(id))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SizeResponse>>> getAll() {
        List<SizeResponse> data = sizeService.getAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Sizes fetched", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        sizeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Size deleted", null));
    }

    private Size toEntity(SizeRequest request) {
        Size size = new Size();
        size.setName(request.getName());
        size.setType(request.getType());
        return size;
    }

    private SizeResponse toResponse(Size size) {
        return SizeResponse.builder()
                .id(size.getId())
                .name(size.getName())
                .type(size.getType())
                .createdAt(size.getCreatedAt())
                .updatedAt(size.getUpdatedAt())
                .build();
    }
}