package com.medresq.backend.dto.response;

public record OtpSentResponse(
        boolean sent,
        String message
) {}
