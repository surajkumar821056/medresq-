package com.medresq.backend.controller;

import com.medresq.backend.dto.request.ReferralRequest;
import com.medresq.backend.dto.response.ReferralResponse;
import com.medresq.backend.service.ReferralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;

    // Public - a patient can submit a booking/referral request without logging in
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReferralResponse create(@Valid @RequestBody ReferralRequest req) {
        return referralService.createReferral(req);
    }

    // Authenticated - list all referrals, optionally filtered by hospital
    @GetMapping
    public List<ReferralResponse> getAll(@RequestParam(required = false) Long hospitalId) {
        return hospitalId != null
                ? referralService.getByHospital(hospitalId)
                : referralService.getAll();
    }

    // Admin only
    @PatchMapping("/{id}/approve")
    public ReferralResponse approve(@PathVariable Long id) {
        return referralService.approve(id);
    }

    // Admin only
    @PatchMapping("/{id}/decline")
    public ReferralResponse decline(@PathVariable Long id) {
        return referralService.decline(id);
    }

    // Admin only
    @PatchMapping("/{id}/admit")
    public ReferralResponse admit(@PathVariable Long id) {
        return referralService.admit(id);
    }

    // Admin only
    @PatchMapping("/{id}/discharge")
    public ReferralResponse discharge(@PathVariable Long id) {
        return referralService.discharge(id);
    }
}
