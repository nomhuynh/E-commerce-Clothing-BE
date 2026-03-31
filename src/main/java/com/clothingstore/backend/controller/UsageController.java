package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.entity.Usage;
import com.clothingstore.backend.service.UsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usages")
@RequiredArgsConstructor
@Tag(name = "Usages")
public class UsageController {

    private final UsageService usageService;

    @Operation(summary = "Get all usages")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Usage>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Usages fetched", usageService.getAll()));
    }

    @Operation(summary = "Get usage by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Usage>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Usage fetched", usageService.getById(id)));
    }

    @Operation(summary = "Create usage")
    @PostMapping
    public ResponseEntity<ApiResponse<Usage>> create(@RequestBody Usage usage) {
        return ResponseEntity.ok(ApiResponse.success("Usage created", usageService.create(usage)));
    }

    @Operation(summary = "Update usage")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Usage>> update(@PathVariable String id, @RequestBody Usage usage) {
        usage.setId(id);
        return ResponseEntity.ok(ApiResponse.success("Usage updated", usageService.update(usage)));
    }

    @Operation(summary = "Delete usage")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        usageService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Usage deleted", null));
    }
}
