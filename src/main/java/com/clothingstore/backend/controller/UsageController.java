package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.usage.UsageRequest;
import com.clothingstore.backend.dto.usage.UsageResponse;
import com.clothingstore.backend.entity.Usage;
import com.clothingstore.backend.service.UsageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usages")
@RequiredArgsConstructor
public class UsageController {

    private final UsageService usageService;

    @PostMapping
    public ResponseEntity<ApiResponse<UsageResponse>> create(@Valid @RequestBody UsageRequest request) {
        Usage usage = toEntity(request);
        Usage created = usageService.create(usage);
        return ResponseEntity.ok(ApiResponse.success("Usage created", toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsageResponse>> update(@PathVariable String id, @Valid @RequestBody UsageRequest request) {
        Usage usage = toEntity(request);
        usage.setId(id);
        Usage updated = usageService.update(usage);
        return ResponseEntity.ok(ApiResponse.success("Usage updated", toResponse(updated)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsageResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Usage fetched", toResponse(usageService.getById(id))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsageResponse>>> getAll() {
        List<UsageResponse> data = usageService.getAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Usages fetched", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        usageService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Usage deleted", null));
    }

    private Usage toEntity(UsageRequest request) {
        Usage usage = new Usage();
        usage.setName(request.getName());
        usage.setDescription(request.getDescription());
        return usage;
    }

    private UsageResponse toResponse(Usage usage) {
        return UsageResponse.builder()
                .id(usage.getId())
                .name(usage.getName())
                .description(usage.getDescription())
                .createdAt(usage.getCreatedAt())
                .updatedAt(usage.getUpdatedAt())
                .build();
    }
}