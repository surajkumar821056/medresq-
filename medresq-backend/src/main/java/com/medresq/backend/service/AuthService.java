package com.medresq.backend.service;

import com.medresq.backend.dto.request.AdminLoginRequest;
import com.medresq.backend.dto.request.SendOtpRequest;
import com.medresq.backend.dto.request.VerifyOtpRequest;
import com.medresq.backend.dto.response.AuthResponse;
import com.medresq.backend.dto.response.OtpSentResponse;
import com.medresq.backend.entity.AppUser;
import com.medresq.backend.entity.enums.Role;
import com.medresq.backend.exception.UnauthorizedException;
import com.medresq.backend.repository.AppUserRepository;
import com.medresq.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * NOTE ON OTP: this mirrors the frontend's current demo behavior - "send OTP"
 * just simulates a delay, and ANY code verifies. There is no real SMS
 * integration here. Swap sendOtp()/verifyOtp() for a real provider
 * (Twilio, MSG91, etc.) plus a proper time-boxed, hashed OTP store when
 * you're ready to go beyond a demo.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // phone -> "sent" marker, just so verify() can confirm send() happened first.
    private final Map<String, Boolean> otpSentTracker = new ConcurrentHashMap<>();

    public OtpSentResponse sendOtp(SendOtpRequest req) {
        otpSentTracker.put(req.phone(), true);
        return new OtpSentResponse(true, "OTP sent (demo mode - enter any code to verify)");
    }

    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest req) {
        if (!Boolean.TRUE.equals(otpSentTracker.get(req.phone()))) {
            throw new UnauthorizedException("Request an OTP before verifying");
        }
        if (req.otp() == null || req.otp().isBlank()) {
            throw new UnauthorizedException("OTP is required");
        }

        AppUser user = appUserRepository.findByPhone(req.phone())
                .orElseGet(() -> appUserRepository.save(
                        AppUser.builder()
                                .name("Patient")
                                .phone(req.phone())
                                .role(Role.PATIENT)
                                .build()
                ));

        otpSentTracker.remove(req.phone());

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse adminLogin(AdminLoginRequest req) {
        AppUser user = appUserRepository.findByEmailIgnoreCase(req.email())
                .filter(u -> u.getRole() == Role.ADMIN)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(AppUser user) {
        String token = jwtService.generateToken(user);
        Long hospitalId = user.getHospital() != null ? user.getHospital().getId() : null;
        return new AuthResponse(
                token, user.getId(), user.getName(), user.getEmail(),
                user.getPhone(), user.getRole(), hospitalId
        );
    }
}
