package com.medresq.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyOtpRequest(
        @NotBlank String phone,
        @NotBlank String otp
) {}
