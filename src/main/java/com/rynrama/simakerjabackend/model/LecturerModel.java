package com.rynrama.simakerjabackend.model;

import jakarta.persistence.*;
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

    public LecturerModel(UUID id, UserModel user, String nidn, String nip, String studyProgram, Boolean isAdhoc) {
        this.id = id;
        this.user = user;
        this.nidn = nidn;
        this.nip = nip;
        this.studyProgram = studyProgram;
        this.isAdhoc = isAdhoc;
    }

    public LecturerModel() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getNidn() {
        return nidn;
    }

    public void setNidn(String nidn) {
        this.nidn = nidn;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getStudyProgram() {
        return studyProgram;
    }

    public void setStudyProgram(String studyProgram) {
        this.studyProgram = studyProgram;
    }

    public Boolean getAdhoc() {
        return isAdhoc;
    }

    public void setAdhoc(Boolean adhoc) {
        isAdhoc = adhoc;
    }
}
