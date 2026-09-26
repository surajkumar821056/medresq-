package com.medresq.backend.service;

import com.medresq.backend.dto.request.BedUpdateRequest;
import com.medresq.backend.dto.request.HospitalRequest;
import com.medresq.backend.dto.response.BedCountDto;
import com.medresq.backend.dto.response.HospitalResponse;
import com.medresq.backend.entity.BedInventory;
import com.medresq.backend.entity.Hospital;
import com.medresq.backend.exception.ResourceNotFoundException;
import com.medresq.backend.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    @Transactional(readOnly = true)
    public List<HospitalResponse> getAllHospitals() {
        return hospitalRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HospitalResponse getHospital(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public HospitalResponse createHospital(HospitalRequest req) {
        Hospital hospital = Hospital.builder()
                .name(req.name())
                .address(req.address())
                .lat(req.lat())
                .lng(req.lng())
                .phone(req.phone())
                .email(req.email())
                .build();
        return toResponse(hospitalRepository.save(hospital));
    }

    @Transactional
    public HospitalResponse updateHospitalProfile(Long id, HospitalRequest req) {
        Hospital hospital = findOrThrow(id);
        hospital.setName(req.name());
        hospital.setAddress(req.address());
        hospital.setLat(req.lat());
        hospital.setLng(req.lng());
        hospital.setPhone(req.phone());
        hospital.setEmail(req.email());
        return toResponse(hospitalRepository.save(hospital));
    }

    @Transactional
    public HospitalResponse updateBeds(Long id, BedUpdateRequest req) {
        Hospital hospital = findOrThrow(id);

        // Validate: available can't exceed total for any entry before applying anything
        for (BedUpdateRequest.Entry entry : req.beds()) {
            if (entry.available() > entry.total()) {
                throw new IllegalArgumentException(
                        "Available beds (" + entry.available() + ") cannot exceed total capacity ("
                                + entry.total() + ") for " + entry.bedType());
            }
        }

        Map<com.medresq.backend.entity.enums.BedType, BedInventory> existing = hospital.getBeds().stream()
                .collect(Collectors.toMap(BedInventory::getBedType, b -> b));

        for (BedUpdateRequest.Entry entry : req.beds()) {
            BedInventory bed = existing.get(entry.bedType());
            if (bed != null) {
                bed.setTotal(entry.total());
                bed.setAvailable(entry.available());
            } else {
                BedInventory newBed = BedInventory.builder()
                        .hospital(hospital)
                        .bedType(entry.bedType())
                        .total(entry.total())
                        .available(entry.available())
                        .build();
                hospital.getBeds().add(newBed);
            }
        }

        return toResponse(hospitalRepository.save(hospital));
    }

    @Transactional
    public void deleteHospital(Long id) {
        if (!hospitalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hospital not found: " + id);
        }
        hospitalRepository.deleteById(id);
    }

    public Hospital findOrThrow(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found: " + id));
    }

    public HospitalResponse toResponse(Hospital h) {
        List<BedCountDto> beds = h.getBeds().stream()
                .map(b -> new BedCountDto(b.getBedType(), b.getTotal(), b.getAvailable()))
                .collect(Collectors.toList());

        return new HospitalResponse(
                h.getId(), h.getName(), h.getAddress(), h.getLat(), h.getLng(),
                h.getPhone(), h.getEmail(), beds, h.getLastUpdated()
        );
    }
}
