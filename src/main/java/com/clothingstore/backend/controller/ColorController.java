package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.entity.Color;
import com.clothingstore.backend.service.ColorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/colors")
@RequiredArgsConstructor
@Tag(name = "Colors")
public class ColorController {

    private final ColorService colorService;

    @Operation(summary = "Get all colors")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Color>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Colors fetched", colorService.getAll()));
    }

    @Operation(summary = "Get color by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Color>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Color fetched", colorService.getById(id)));
    }

    @Operation(summary = "Create color")
    @PostMapping
    public ResponseEntity<ApiResponse<Color>> create(@RequestBody Color color) {
        return ResponseEntity.ok(ApiResponse.success("Color created", colorService.create(color)));
    }

    @Operation(summary = "Update color")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Color>> update(@PathVariable String id, @RequestBody Color color) {
        color.setId(id);
        return ResponseEntity.ok(ApiResponse.success("Color updated", colorService.update(color)));
    }

    @Operation(summary = "Delete color")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        colorService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Color deleted", null));
    }
}
