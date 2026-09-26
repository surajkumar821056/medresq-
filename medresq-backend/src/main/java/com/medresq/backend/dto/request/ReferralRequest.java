package com.medresq.backend.dto.request;

import com.medresq.backend.entity.enums.BedType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReferralRequest(
        @NotNull Long hospitalId,
        @NotBlank String patientName,
        Integer age,
        String symptom,
        @NotNull BedType bedType,
        String pulse,
        String bloodPressure,
        String spo2,
        Double lat,
        Double lng
) {}
