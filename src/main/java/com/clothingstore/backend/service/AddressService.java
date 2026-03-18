package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Address;

import java.util.List;

public interface AddressService {
    Address create(Address address);
    Address update(Address address);
    Address getById(String id);
    List<Address> getByUserId(String userId);
    List<Address> getAll();
    void delete(String id);
}
