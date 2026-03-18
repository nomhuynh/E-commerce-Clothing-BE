package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.address.AddressRequest;
import com.clothingstore.backend.dto.address.AddressResponse;
import com.clothingstore.backend.entity.Address;
import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(@Valid @RequestBody AddressRequest request) {
        Address created = addressService.create(toEntity(request));
        return ResponseEntity.ok(ApiResponse.success("Address created", toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> update(@PathVariable String id, @Valid @RequestBody AddressRequest request) {
        Address entity = toEntity(request);
        entity.setId(id);
        Address updated = addressService.update(entity);
        return ResponseEntity.ok(ApiResponse.success("Address updated", toResponse(updated)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Address fetched", toResponse(addressService.getById(id))));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getByUserId(@PathVariable String userId) {
        List<AddressResponse> data = addressService.getByUserId(userId).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.success("Addresses fetched", data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAll() {
        List<AddressResponse> data = addressService.getAll().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.success("Addresses fetched", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        addressService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted", null));
    }

    private Address toEntity(AddressRequest request) {
        Address address = new Address();
        User user = new User();
        user.setId(request.getUserId());
        address.setUser(user);
        address.setRecipientName(request.getRecipientName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setStreetAddress(request.getStreetAddress());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setCity(request.getCity());
        if (request.getIsDefault() != null) {
            address.setIsDefault(request.getIsDefault());
        }
        return address;
    }

    private AddressResponse toResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .userId(address.getUser() != null ? address.getUser().getId() : null)
                .recipientName(address.getRecipientName())
                .phoneNumber(address.getPhoneNumber())
                .streetAddress(address.getStreetAddress())
                .ward(address.getWard())
                .district(address.getDistrict())
                .city(address.getCity())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
