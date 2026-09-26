package com.medresq.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SendOtpRequest(
        @NotBlank @Pattern(regexp = "\\d{10}", message = "Phone must be exactly 10 digits") String phone
) {}
