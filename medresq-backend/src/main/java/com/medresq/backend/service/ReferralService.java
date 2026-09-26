package com.medresq.backend.service;

import com.medresq.backend.dto.request.ReferralRequest;
import com.medresq.backend.dto.response.ReferralResponse;
import com.medresq.backend.entity.BedInventory;
import com.medresq.backend.entity.Hospital;
import com.medresq.backend.entity.Referral;
import com.medresq.backend.entity.enums.BedType;
import com.medresq.backend.entity.enums.ReferralStatus;
import com.medresq.backend.exception.BadRequestException;
import com.medresq.backend.exception.ResourceNotFoundException;
import com.medresq.backend.repository.ReferralRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferralService {

    private final ReferralRepository referralRepository;
    private final HospitalService hospitalService;

    @Transactional
    public ReferralResponse createReferral(ReferralRequest req) {
        Hospital hospital = hospitalService.findOrThrow(req.hospitalId());

        Referral referral = Referral.builder()
                .hospital(hospital)
                .patientName(req.patientName())
                .age(req.age())
                .symptom(req.symptom())
                .bedType(req.bedType())
                .status(ReferralStatus.PENDING)
                .pulse(req.pulse())
                .bloodPressure(req.bloodPressure())
                .spo2(req.spo2())
                .lat(req.lat())
                .lng(req.lng())
                .build();

        return toResponse(referralRepository.save(referral));
    }

    @Transactional(readOnly = true)
    public List<ReferralResponse> getAll() {
        return referralRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferralResponse> getByHospital(Long hospitalId) {
        return referralRepository.findByHospitalIdOrderByCreatedAtDesc(hospitalId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    /** Approve a pending referral: deducts one bed of the requested type from the hospital's inventory. */
    @Transactional
    public ReferralResponse approve(Long referralId) {
        Referral referral = findOrThrow(referralId);
        Hospital hospital = referral.getHospital();
        BedType type = referral.getBedType();

        BedInventory bed = hospital.getBeds().stream()
                .filter(b -> b.getBedType() == type)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Hospital does not offer " + type + " beds"));

        if (bed.getAvailable() <= 0) {
            throw new BadRequestException("No available " + type + " beds to allocate");
        }

        bed.setAvailable(bed.getAvailable() - 1);
        referral.setStatus(ReferralStatus.APPROVED);

        return toResponse(referralRepository.save(referral));
    }

    @Transactional
    public ReferralResponse decline(Long referralId) {
        Referral referral = findOrThrow(referralId);
        referral.setStatus(ReferralStatus.REJECTED);
        return toResponse(referralRepository.save(referral));
    }

    @Transactional
    public ReferralResponse admit(Long referralId) {
        Referral referral = findOrThrow(referralId);
        if (referral.getStatus() != ReferralStatus.APPROVED) {
            throw new BadRequestException("Only approved referrals can be admitted");
        }
        referral.setStatus(ReferralStatus.ADMITTED);
        return toResponse(referralRepository.save(referral));
    }

    /** Discharge a patient: releases the bed back into the hospital's available inventory. */
    @Transactional
    public ReferralResponse discharge(Long referralId) {
        Referral referral = findOrThrow(referralId);
        Hospital hospital = referral.getHospital();
        BedType type = referral.getBedType();

        BedInventory bed = hospital.getBeds().stream()
                .filter(b -> b.getBedType() == type)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Hospital does not offer " + type + " beds"));

        bed.setAvailable(Math.min(bed.getTotal(), bed.getAvailable() + 1));
        referral.setStatus(ReferralStatus.DISCHARGED);

        return toResponse(referralRepository.save(referral));
    }

    private Referral findOrThrow(Long id) {
        return referralRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Referral not found: " + id));
    }

    private ReferralResponse toResponse(Referral r) {
        return new ReferralResponse(
                r.getId(), r.getHospital().getId(), r.getHospital().getName(),
                r.getPatientName(), r.getAge(), r.getSymptom(), r.getBedType(), r.getStatus(),
                r.getPulse(), r.getBloodPressure(), r.getSpo2(), r.getLat(), r.getLng(),
                r.getCreatedAt(), r.getUpdatedAt()
        );
    }
}
