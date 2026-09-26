package com.medresq.backend.seed;

import com.medresq.backend.entity.AppUser;
import com.medresq.backend.entity.BedInventory;
import com.medresq.backend.entity.Hospital;
import com.medresq.backend.entity.enums.BedType;
import com.medresq.backend.entity.enums.Role;
import com.medresq.backend.repository.AppUserRepository;
import com.medresq.backend.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the same 12 Delhi NCR hospitals used in the React frontend's
 * src/utils/mockData.js, so the backend is a drop-in replacement for the
 * localStorage-based mock data. Also creates a matching admin login for
 * each hospital (password: "admin", same as the current frontend demo).
 *
 * Only runs if the hospitals table is empty, so it's safe across restarts.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final HospitalRepository hospitalRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    private record SeedHospital(String name, String address, double lat, double lng,
                                 String phone, String email, int[] general, int[] icu,
                                 int[] picu, int[] nicu, int[] ventilator, int[] isolation) {}

    @Override
    public void run(String... args) {
        if (hospitalRepository.count() > 0) {
            return;
        }

        List<SeedHospital> seedData = List.of(
            new SeedHospital("AIIMS New Delhi", "Ansari Nagar, New Delhi, Delhi 110029", 28.5672, 77.2100,
                "+91 11 2658 8500", "emergency@aiims.edu",
                new int[]{150, 42}, new int[]{40, 4}, new int[]{20, 6}, new int[]{25, 11}, new int[]{30, 3}, new int[]{20, 8}),
            new SeedHospital("Safdarjung Hospital Delhi", "Ansari Nagar East, New Delhi, Delhi 110029", 28.5684, 77.2072,
                "+91 11 2673 0000", "admin@safdarjunghospital.gov.in",
                new int[]{100, 12}, new int[]{25, 2}, new int[]{10, 1}, new int[]{15, 3}, new int[]{15, 1}, new int[]{15, 2}),
            new SeedHospital("Max Super Speciality Hospital Saket", "Press Enclave Marg, Saket, New Delhi, Delhi 110017", 28.5284, 77.2114,
                "+91 11 2651 5050", "info@maxhealthcare.com",
                new int[]{80, 28}, new int[]{15, 8}, new int[]{5, 3}, new int[]{10, 7}, new int[]{8, 4}, new int[]{12, 5}),
            new SeedHospital("Fortis Hospital Noida", "B-22, Sector 62, Noida, Uttar Pradesh 201301", 28.6189, 77.3734,
                "+91 120 430 0222", "contact@fortisnoida.com",
                new int[]{120, 55}, new int[]{30, 12}, new int[]{15, 9}, new int[]{20, 15}, new int[]{20, 8}, new int[]{10, 7}),
            new SeedHospital("Sharda Hospital Greater Noida", "Plot 32, Knowledge Park III, Greater Noida, Uttar Pradesh 201306", 28.4731, 77.4828,
                "+91 120 232 9700", "info@shardahospital.org",
                new int[]{90, 8}, new int[]{20, 1}, new int[]{8, 0}, new int[]{12, 2}, new int[]{10, 0}, new int[]{8, 1}),
            new SeedHospital("Kailash Hospital Greater Noida", "Plot 23, Knowledge Park I, Greater Noida, Uttar Pradesh 201310", 28.4789, 77.4912,
                "+91 120 232 7700", "kailash@kailashhospital.com",
                new int[]{110, 45}, new int[]{25, 6}, new int[]{10, 4}, new int[]{15, 9}, new int[]{12, 3}, new int[]{10, 6}),
            new SeedHospital("Jaypee Hospital Noida", "Wish Town, Sector 128, Noida, Uttar Pradesh 201304", 28.5147, 77.3712,
                "+91 120 412 2222", "emergency@jaypeehealthcare.com",
                new int[]{130, 35}, new int[]{35, 14}, new int[]{12, 8}, new int[]{18, 11}, new int[]{15, 7}, new int[]{12, 9}),
            new SeedHospital("Medanta - The Medicity Gurugram", "Sector 38, Gurugram, Haryana 122001", 28.4312, 77.0425,
                "+91 124 414 1414", "info@medanta.org",
                new int[]{200, 48}, new int[]{50, 18}, new int[]{15, 5}, new int[]{20, 12}, new int[]{25, 10}, new int[]{15, 8}),
            new SeedHospital("Sir Ganga Ram Hospital New Delhi", "Sir Ganga Ram Hospital Marg, Rajinder Nagar, New Delhi, Delhi 110060", 28.6382, 77.1895,
                "+91 11 2575 0000", "gangaram@sgrh.com",
                new int[]{140, 22}, new int[]{30, 5}, new int[]{10, 3}, new int[]{15, 8}, new int[]{15, 2}, new int[]{12, 4}),
            new SeedHospital("Indraprastha Apollo Hospitals New Delhi", "Sarita Vihar, Delhi Mathura Road, New Delhi, Delhi 110076", 28.5361, 77.2861,
                "+91 11 2987 1090", "apollo_delhi@apollohospitals.com",
                new int[]{180, 40}, new int[]{45, 11}, new int[]{15, 7}, new int[]{20, 14}, new int[]{20, 6}, new int[]{15, 9}),
            new SeedHospital("Fortis Escorts Heart Institute New Delhi", "Okhla Road, Sukhdev Vihar, New Delhi, Delhi 110025", 28.5587, 77.2783,
                "+91 11 4713 5000", "contact.escorts@fortishealthcare.com",
                new int[]{100, 15}, new int[]{25, 3}, null, null, new int[]{12, 1}, null),
            new SeedHospital("Metro Hospital & Heart Institute Noida", "X-1, Sector 12, Noida, Uttar Pradesh 201301", 28.5912, 77.3325,
                "+91 120 422 6666", "info@metrohospitals.com",
                new int[]{110, 32}, new int[]{20, 7}, new int[]{8, 4}, new int[]{12, 8}, new int[]{10, 4}, new int[]{8, 5})
        );

        for (SeedHospital s : seedData) {
            Hospital hospital = Hospital.builder()
                    .name(s.name())
                    .address(s.address())
                    .lat(s.lat())
                    .lng(s.lng())
                    .phone(s.phone())
                    .email(s.email())
                    .beds(new ArrayList<>())
                    .build();

            addBed(hospital, BedType.GENERAL, s.general());
            addBed(hospital, BedType.ICU, s.icu());
            addBed(hospital, BedType.PICU, s.picu());
            addBed(hospital, BedType.NICU, s.nicu());
            addBed(hospital, BedType.VENTILATOR, s.ventilator());
            addBed(hospital, BedType.ISOLATION, s.isolation());

            hospital = hospitalRepository.save(hospital);

            // Seed a matching admin account: email as above, password "admin"
            // (mirrors the current frontend demo credentials).
            appUserRepository.save(AppUser.builder()
                    .name("Hospital Admin")
                    .email(s.email())
                    .role(Role.ADMIN)
                    .hospital(hospital)
                    .passwordHash(passwordEncoder.encode("admin"))
                    .build());
        }
    }

    private void addBed(Hospital hospital, BedType type, int[] totalAvailable) {
        if (totalAvailable == null) return; // some hospitals don't offer this bed type
        hospital.getBeds().add(BedInventory.builder()
                .hospital(hospital)
                .bedType(type)
                .total(totalAvailable[0])
                .available(totalAvailable[1])
                .build());
    }
}
