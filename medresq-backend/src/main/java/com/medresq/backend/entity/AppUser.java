package com.medresq.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medresq.backend.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // For ADMIN: hospital admin email. For PATIENT: nullable.
    @Column(unique = true)
    private String email;

    // For PATIENT: 10-digit mobile number. For ADMIN: nullable.
    @Column(unique = true)
    private String phone;

    // Only set for ADMIN role (BCrypt hash). Patients authenticate via OTP, no password.
    @JsonIgnore
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // For ADMIN users: which hospital they manage.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }
}
