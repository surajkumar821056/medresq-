package com.medresq.backend.controller;

import com.medresq.backend.dto.request.BedUpdateRequest;
import com.medresq.backend.dto.request.HospitalRequest;
import com.medresq.backend.dto.response.HospitalResponse;
import com.medresq.backend.service.HospitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping
    public List<HospitalResponse> getAll() {
        return hospitalService.getAllHospitals();
    }

    @GetMapping("/{id}")
    public HospitalResponse getOne(@PathVariable Long id) {
        return hospitalService.getHospital(id);
    }

    // Admin only (see SecurityConfig) - onboard a new hospital
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HospitalResponse create(@Valid @RequestBody HospitalRequest req) {
        return hospitalService.createHospital(req);
    }

    // Admin only - update a hospital's profile (name, address, contact info)
    @PutMapping("/{id}")
    public HospitalResponse updateProfile(@PathVariable Long id, @Valid @RequestBody HospitalRequest req) {
        return hospitalService.updateHospitalProfile(id, req);
    }

    // Admin only - update live bed inventory counts
    @PutMapping("/{id}/beds")
    public HospitalResponse updateBeds(@PathVariable Long id, @Valid @RequestBody BedUpdateRequest req) {
        return hospitalService.updateBeds(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hospitalService.deleteHospital(id);
        return ResponseEntity.noContent().build();
    }
}
