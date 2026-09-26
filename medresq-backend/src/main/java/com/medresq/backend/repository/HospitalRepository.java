package com.medresq.backend.repository;

import com.medresq.backend.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Optional<Hospital> findByEmailIgnoreCase(String email);
}
