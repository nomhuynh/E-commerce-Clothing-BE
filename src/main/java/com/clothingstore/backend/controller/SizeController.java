package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.entity.Size;
import com.clothingstore.backend.service.SizeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sizes")
@RequiredArgsConstructor
@Tag(name = "Sizes")
public class SizeController {

    private final SizeService sizeService;

    @Operation(summary = "Get all sizes")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Size>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Sizes fetched", sizeService.getAll()));
    }

    @Operation(summary = "Get size by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Size>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Size fetched", sizeService.getById(id)));
    }

    @Operation(summary = "Create size")
    @PostMapping
    public ResponseEntity<ApiResponse<Size>> create(@RequestBody Size size) {
        return ResponseEntity.ok(ApiResponse.success("Size created", sizeService.create(size)));
    }

    @Operation(summary = "Update size")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Size>> update(@PathVariable String id, @RequestBody Size size) {
        size.setId(id);
        return ResponseEntity.ok(ApiResponse.success("Size updated", sizeService.update(size)));
    }

    @Operation(summary = "Delete size")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        sizeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Size deleted", null));
    }
}
