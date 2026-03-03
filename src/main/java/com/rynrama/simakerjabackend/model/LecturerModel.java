package com.rynrama.simakerjabackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(
        name = "lecturers",
        indexes = {
                @Index(name = "idx_nidn", columnList = "nidn"),
                @Index(name = "idx_nip", columnList = "nip")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_lecturers_user", columnNames = "user_id"),
                @UniqueConstraint(name = "uk_lecturers_nidn", columnNames = "nidn"),
                @UniqueConstraint(name = "uk_lecturers_nip", columnNames = "nip")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LecturerModel {

    @Id
    @UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_lecturers_user")
    )
    private UserModel user;

    @Column(unique = true, length = 20)
    private String nidn;

    @Column(unique = true, length = 20)
    private String nip;

    @Column(name = "study_program")
    private String studyProgram;

    @Column(name = "is_adhoc")
    private Boolean isAdhoc = false;
}
