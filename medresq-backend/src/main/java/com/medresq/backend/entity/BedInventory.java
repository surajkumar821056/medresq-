package com.medresq.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medresq.backend.entity.enums.BedType;
import jakarta.persistence.*;
import lombok.*;

/**
 * One row per (hospital, bedType). Absence of a row means that hospital
 * doesn't offer that bed type at all (mirrors the `null` entries seen in
 * the frontend mock data, e.g. some hospitals have no PICU).
 */
@Entity
@Table(name = "bed_inventory", uniqueConstraints = @UniqueConstraint(columnNames = {"hospital_id", "bed_type"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    @JsonIgnore
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_type", nullable = false)
    private BedType bedType;

    @Column(nullable = false)
    private Integer total;

    @Column(nullable = false)
    private Integer available;
}
