package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.upload.UploadedImageDto;
import com.clothingstore.backend.service.CloudinaryUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/upload")
@RequiredArgsConstructor
public class AdminUploadController {

    private static final String PRODUCTS_FOLDER = "stylex/products";

    private final CloudinaryUploadService cloudinaryUploadService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadedImageDto>> uploadProductImage(
            @RequestPart("file") MultipartFile file) {
        String url = cloudinaryUploadService.uploadImage(file, PRODUCTS_FOLDER);
        return ResponseEntity.ok(ApiResponse.success(
                "Image uploaded",
                UploadedImageDto.builder().url(url).build()));
    }
}
