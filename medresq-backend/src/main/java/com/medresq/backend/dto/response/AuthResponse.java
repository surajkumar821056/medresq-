package com.medresq.backend.dto.response;

import com.medresq.backend.entity.enums.Role;

public record AuthResponse(
        String token,
        Long userId,
        String name,
        String email,
        String phone,
        Role role,
        Long hospitalId
) {}
