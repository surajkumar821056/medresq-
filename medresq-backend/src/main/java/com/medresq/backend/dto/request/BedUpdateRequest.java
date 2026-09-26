package com.medresq.backend.dto.request;

import com.medresq.backend.entity.enums.BedType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BedUpdateRequest(
        @NotNull List<Entry> beds
) {
    public record Entry(
            @NotNull BedType bedType,
            @NotNull @Min(0) Integer total,
            @NotNull @Min(0) Integer available
    ) {}
}
