package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.entity.Address;
import com.clothingstore.backend.repository.AddressRepository;
import com.clothingstore.backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public Address create(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public Address update(Address address) {
        if (address.getId() == null) {
            throw new RuntimeException("Address id is required for update");
        }
        if (!addressRepository.existsById(address.getId())) {
            throw new RuntimeException("Address not found");
        }
        return addressRepository.save(address);
    }

    @Override
    public Address getById(String id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }

    @Override
    public List<Address> getByUserId(String userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    public List<Address> getAll() {
        return addressRepository.findAll();
    }

    @Override
    public void delete(String id) {
        if (!addressRepository.existsById(id)) {
            throw new RuntimeException("Address not found");
        }
        addressRepository.deleteById(id);
    }
}
