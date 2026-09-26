package com.medresq.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HospitalRequest(
        @NotBlank String name,
        @NotBlank String address,
        @NotNull Double lat,
        @NotNull Double lng,
        String phone,
        String email
) {}
