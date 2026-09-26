package com.medresq.backend.repository;

import com.medresq.backend.entity.Referral;
import com.medresq.backend.entity.enums.ReferralStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findByHospitalIdOrderByCreatedAtDesc(Long hospitalId);
    List<Referral> findByHospitalIdAndStatusOrderByCreatedAtDesc(Long hospitalId, ReferralStatus status);
    List<Referral> findAllByOrderByCreatedAtDesc();
    List<Referral> findByRequestedByIdOrderByCreatedAtDesc(Long userId);
}
