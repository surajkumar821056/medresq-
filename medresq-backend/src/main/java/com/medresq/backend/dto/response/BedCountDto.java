package com.medresq.backend.dto.response;

import com.medresq.backend.entity.enums.BedType;

public record BedCountDto(
        BedType bedType,
        Integer total,
        Integer available
) {}
