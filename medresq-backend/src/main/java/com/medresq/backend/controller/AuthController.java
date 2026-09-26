package com.medresq.backend.controller;

import com.medresq.backend.dto.request.AdminLoginRequest;
import com.medresq.backend.dto.request.SendOtpRequest;
import com.medresq.backend.dto.request.VerifyOtpRequest;
import com.medresq.backend.dto.response.AuthResponse;
import com.medresq.backend.dto.response.OtpSentResponse;
import com.medresq.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/patient/send-otp")
    public OtpSentResponse sendOtp(@Valid @RequestBody SendOtpRequest req) {
        return authService.sendOtp(req);
    }

    @PostMapping("/patient/verify-otp")
    public AuthResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest req) {
        return authService.verifyOtp(req);
    }

    @PostMapping("/admin/login")
    public AuthResponse adminLogin(@Valid @RequestBody AdminLoginRequest req) {
        return authService.adminLogin(req);
    }
}
