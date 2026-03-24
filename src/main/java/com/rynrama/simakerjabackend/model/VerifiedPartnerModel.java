package com.rynrama.simakerjabackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.cglib.core.Local;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(
        name = "verified_partners",
        indexes = {
                @Index(name = "idx_verified_partners_partner_number", columnList = "partner_number"),
                @Index(name = "idx_verified_partner_name_trgm", columnList = "partner_name_normalized"),
                @Index(name = "idx_verified_partner_acronym", columnList = "partner_name_acronym")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_verified_partners_moa_ia", columnNames = "moa_ia_id"),
                @UniqueConstraint(name = "uk_verified_partners_partner_name", columnNames = "partner_name"),
                @UniqueConstraint(name = "uk_verified_partners_partner_number", columnNames = "partner_number")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VerifiedPartnerModel {
    @Id
    @UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "moa_ia_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_verified_partners_moa_ia")
    )
    private MoaIADocumentModel moaIa;

    @Column(name = "partner_name", nullable = false)
    private String partnerName;

    @Column(name = "partner_number", length = 50)
    private String partnerNumber;

    @Column(name = "faculty_representative_name", nullable = false)
    private String facultyRepresentativeName;

    @Column(name = "partner_representative_name", nullable = false)
    private String partnerRepresentativeName;

    @Column(name = "partner_representative_position", nullable = false)
    private String partnerRepresentativePosition;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_activity", nullable = false)
    private DocumentActivityType activityType;

    @Column(name = "partner_logo_key", nullable = false)
    private String partnerLogoKey;

    @Column(name = "partner_address", nullable = false)
    private String partnerAddress;

    @Column(name = "partner_cooperation_period", nullable = false)
    private Integer partnerCooperationPeriod = 1;

    @Column(name = "verified_at", nullable = false, updatable = false)
    private Instant verifiedAt;

    @Column(name = "verified_until", nullable = false, updatable = false)
    private Instant verifiedUntil;

    @Column(name = "partner_name_normalized")
    private String partnerNameNormalized;

    @Column(name = "partner_name_acronym")
    private String partnerNameAcronym;
}
