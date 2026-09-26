package com.medresq.backend.dto.response;

import com.medresq.backend.entity.enums.BedType;
import com.medresq.backend.entity.enums.ReferralStatus;

import java.time.Instant;

public record ReferralResponse(
        Long id,
        Long hospitalId,
        String hospitalName,
        String patientName,
        Integer age,
        String symptom,
        BedType bedType,
        ReferralStatus status,
        String pulse,
        String bloodPressure,
        String spo2,
        Double lat,
        Double lng,
        Instant createdAt,
        Instant updatedAt
) {}
