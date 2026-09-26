package com.medresq.backend.dto.response;

import java.time.Instant;
import java.util.List;

public record HospitalResponse(
        Long id,
        String name,
        String address,
        Double lat,
        Double lng,
        String phone,
        String email,
        List<BedCountDto> beds,
        Instant lastUpdated
) {}
