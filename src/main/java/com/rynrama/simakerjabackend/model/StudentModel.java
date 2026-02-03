package com.rynrama.simakerjabackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(
        name = "students",
        indexes = {
                @Index(name = "idx_nim", columnList = "nim")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_students_user", columnNames = "user_id"),
                @UniqueConstraint(name = "uk_students_nim", columnNames = "nim")
        }
)
public class StudentModel {

    @Id
    @UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_students_user")
    )
    private UserModel user;

    @Column(unique = true, nullable = false, length = 12)
    private String nim;

    @Column(name = "study_program", nullable = false)
    private String studyProgram;

    public StudentModel() {
    }

    public StudentModel(UUID id, UserModel user, String nim, String studyProgram) {
        this.id = id;
        this.user = user;
        this.nim = nim;
        this.studyProgram = studyProgram;
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

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getStudyProgram() {
        return studyProgram;
    }

    public void setStudyProgram(String studyProgram) {
        this.studyProgram = studyProgram;
    }
}
