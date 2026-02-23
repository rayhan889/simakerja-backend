package com.rynrama.simakerjabackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(
        name = "student_snapshot_students",
        indexes = {
                @Index(name = "idx_snapshot_student_nim", columnList = "nim")
        }
)
public class StudentSnapshotStudentModel {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "snapshot_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_snapshot_students_snapshot")
    )
    private StudentSnapshotModel snapshot;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nim", nullable = false, length = 12)
    private String nim;

    public StudentSnapshotStudentModel() {
    }

    public StudentSnapshotStudentModel(String fullName, String email, String nim) {
        this.fullName = fullName;
        this.email = email;
        this.nim = nim;
    }

    public UUID getId() {
        return id;
    }

    public StudentSnapshotModel getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(StudentSnapshotModel snapshot) {
        this.snapshot = snapshot;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }
}
