package com.medresq.backend.repository;

import com.medresq.backend.entity.BedInventory;
import com.medresq.backend.entity.enums.BedType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BedInventoryRepository extends JpaRepository<BedInventory, Long> {
    Optional<BedInventory> findByHospitalIdAndBedType(Long hospitalId, BedType bedType);
}
